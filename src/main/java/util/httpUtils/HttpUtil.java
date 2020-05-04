package util.httpUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import controller.ProgressBarWindow;
import controller.ViewerPane;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import util.TaskThreadPools;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static void doGetPageImages(List<CloudImageNote> fileTreeCloudImageNotes,int page, int size) {
        ViewerPane.progressBarWindow.clearBar();
        try {
            List<CloudImageNote> cloudImageNoteList = new ArrayList<>();
            URIBuilder builder = new URIBuilder(URI_LOCALHOST + "/getImagesDivideIntoPages");
            //set the params of PAGE
            List<NameValuePair> params= new ArrayList<>();
            params.add(new BasicNameValuePair("page",String.valueOf(page)));
            params.add(new BasicNameValuePair("size",String.valueOf(size)));
            builder.addParameters(params);
            //假进度条刷新
            ProgressBarWindow.updateProgressBar(1,size);
            //do GET
            HttpGet httpGet = new HttpGet(builder.build());
            CloseableHttpResponse response = client.execute(httpGet);
            //
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new ConnectException();
            }
            //the response to json string
            HttpEntity entity = response.getEntity();
            String jsonImagesStrings = EntityUtils.toString(entity, "utf-8");
            //to IMAGES
            long loadingSize = jsonImagesStrings.length();
            JSONArray imagesArray = JSONArray.parseArray(jsonImagesStrings);
            for(Object o : imagesArray){
                JSONObject image = (JSONObject) o;
                String id = image.getString("id");
                String fileName = image.getString("fileName");
                FileCode.decodeBASE64(image.getString(
                        "imageString"),
                        System.getProperty("user.dir") + "/cloudAlbum" + "/" + fileName,
                        loadingSize);
                cloudImageNoteList.add(new CloudImageNote(Integer.parseInt(id),fileName));
            }
            //add to fileTree
            fileTreeCloudImageNotes.addAll(cloudImageNoteList);
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * do the  http post method to save the images to database
     * @param paths
     * @throws URISyntaxException
     */
    public static void doPostJson(List<String> paths) throws URISyntaxException, IOException {
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
                Double progress = progressBar.getProgress();
                progressBar.setProgress(progress + 1.0 / paths.size());
            });
        }
        HttpEntity httpEntity = entityBuilder.build();
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println("POST状态" + response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
            throw new ConnectException();
        }
    }

    public static void doDelete(List<CloudImageNote> deleteCloudImageNoteList){

    }
}
