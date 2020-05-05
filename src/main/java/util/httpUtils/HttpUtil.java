package util.httpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import controller.FileTree;
import controller.ProgressBarWindow;
import controller.ViewerPane;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import model.CloudImageNote;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import util.httpUtils.exception.RequestConnectException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
/**a
 * @since  2020/4/26
 * @author Hi lu
 *
 * 程序结束要把未保存的图片删去
 */
public class HttpUtil {
    public static final CloseableHttpClient  client = HttpClientBuilder.create().build();
    private static final String URI_LOCALHOST = "http://localhost:8080/myImages";//硬编程
    private static final String URI_SPRINGBOOT = "http://139.199.66.139:8080/myImages";

    /**
     * do the http get method to request which of images divided into page
     * @param fileTreeCloudImageNotes 文件树提供，每次执行do get，文件树的fileTreeCloudImageNotes增添新数据
     * @param page do get 第几页，从0开始
     * @param size do get 每页大小，最小为1
     */
    public static void doGetPageImages(List<CloudImageNote> fileTreeCloudImageNotes, int page, int size)
            throws RequestConnectException, URISyntaxException {
        ViewerPane.progressBarWindow.clearBar();
        List<CloudImageNote> cloudImageNoteList = new ArrayList<>();
        URIBuilder builder = new URIBuilder(URI_SPRINGBOOT + "/getImagesDivideIntoPages");
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(60000).build();
        //set the params of PAGE
        List<NameValuePair> params= new ArrayList<>();
        params.add(new BasicNameValuePair("page",String.valueOf(page)));
        params.add(new BasicNameValuePair("size",String.valueOf(size)));
        builder.addParameters(params);
        //假进度条刷新
        ProgressBarWindow.updateProgressBar(1,size);
        //do GET
        HttpGet httpGet = new HttpGet(builder.build());
        httpGet.setConfig(requestConfig);
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                response.close();
                ViewerPane.progressBarWindow.setErrorBar();
                throw new RequestConnectException(response.getStatusLine().getStatusCode() + "/t下载失败，是否重试？");
            }
            //the response to json string
            HttpEntity entity = response.getEntity();
            String jsonImagesStrings = EntityUtils.toString(entity, "utf-8");
            //to IMAGES
            long loadingSize = jsonImagesStrings.length();
            JSONArray imagesArray = JSONArray.parseArray(jsonImagesStrings);
            for (Object o : imagesArray) {
                JSONObject image = (JSONObject) o;
                String id = image.getString("id");
                String fileName = image.getString("fileName");
                FileCode.decodeBASE64(image.getString(
                        "imageString"),
                        System.getProperty("user.dir") + "/cloudAlbum" + "/" + fileName,
                        loadingSize);
                cloudImageNoteList.add(new CloudImageNote(Integer.parseInt(id), fileName));
            }
            //add to fileTree
            fileTreeCloudImageNotes.addAll(cloudImageNoteList);
        } catch (IOException exception) {
            throw new RequestConnectException("连接失败，是否重试？");
        }
    }

    /**
     * do the  http post method to save the images to database
     * @param paths
     * @throws URISyntaxException
     */
    public static void doPostJson(List<String> paths) throws RequestConnectException, URISyntaxException {
        ViewerPane.progressBarWindow.clearBar();
        URIBuilder builder = new URIBuilder(URI_LOCALHOST + "/addImages");
        HttpPost httpPost = new HttpPost(builder.build());
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.RFC6532);
        for (String path : paths){
            ContentBody fileBody = new FileBody(new File(path));
            entityBuilder.addPart("files", fileBody);
            Platform.runLater(()->{
                ProgressBar progressBar = ViewerPane.progressBarWindow.getProgressBar();
                double progress = progressBar.getProgress();
                progressBar.setProgress(progress + 1.0 / paths.size());
            });
        }
        HttpEntity httpEntity = entityBuilder.build();
        httpPost.setEntity(httpEntity);
        try(CloseableHttpResponse response = client.execute(httpPost)){
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new RequestConnectException(response.getStatusLine().getStatusCode() + "/t上传失败，是否重试？");
          }
        }catch (IOException exception){
            throw new RequestConnectException("连接失败，是否重试？");
        }
    }

    public static void doDelete(List<File> deleteCloudImageFiles) throws URISyntaxException, RequestConnectException {
        if (deleteCloudImageFiles != null &&deleteCloudImageFiles.size() > 0){
            StringBuilder deleteImagesString = new StringBuilder();
            URIBuilder builder = new URIBuilder(URI_LOCALHOST + "/deleteImages");
            List<CloudImageNote> fileTreeCloudImageNotes = FileTree.cloudImageNoteList;
            for(File file : deleteCloudImageFiles){
                int id = -1;
                for (CloudImageNote note : fileTreeCloudImageNotes){
                    id = note.matchingIdByName(file.getName());
                    if (id != -1) break;
                }
                deleteImagesString.append(",").append(id);
            }
            List<NameValuePair> params= new ArrayList<>();
            params.add(new BasicNameValuePair("ids",deleteImagesString.toString()));
            builder.addParameters(params);

            System.out.println(builder.build().toString());

            HttpDelete httpDelete = new HttpDelete(builder.build());
            try (CloseableHttpResponse response = client.execute(httpDelete)){
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    response.close();
                    throw new RequestConnectException(response.getStatusLine().getStatusCode() + "/t删除失败，是否重试？");
                }
            }catch (IOException exception){
                throw new RequestConnectException("连接失败，是否重试？");
            }
        }
    }
}
