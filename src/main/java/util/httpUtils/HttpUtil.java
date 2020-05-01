package util.httpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
/**a
 * @date  2020/4/26
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
    public  void doGetPageImages(List<CloudImageNote> fileTreeCloudImageNotes,int page, int size) {
        try {
            List<CloudImageNote> cloudImageNoteList = new ArrayList<>();
            URIBuilder builder = new URIBuilder(URI_LOCALHOST + "/getImagesDivideIntoPages");
            //set the params of PAGE
            List<NameValuePair> params= new ArrayList<>();
            params.add(new BasicNameValuePair("page",String.valueOf(page)));
            params.add(new BasicNameValuePair("size",String.valueOf(size)));
            builder.addParameters(params);
            HttpGet httpGet = new HttpGet(builder.build());
            //do GET
            CloseableHttpResponse response = client.execute(httpGet);
            //the response to json string
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("GET状态码:"+statusCode);
            HttpEntity entity = response.getEntity();
            final long loadingSize = entity.getContentLength();
            String jsonImagesStrings = EntityUtils.toString(entity, "utf-8");
            //to IMAGES
            JSONArray imagesArray = JSONArray.parseArray(jsonImagesStrings);
            for(Object o : imagesArray){
                JSONObject image = (JSONObject) o;
                String id = image.getString("id");
                String targetPath = System.getProperty("user.dir") + "/cloudAlbum" + "/cloudImage" + id + ".jpg";
                FileCode.decodeBASE64(image.getString("imageString"), targetPath,loadingSize);
                cloudImageNoteList.add(new CloudImageNote(Integer.parseInt(id)));
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
    public static void doPostJson(String[] paths) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(URI_LOCALHOST + "/addImages");
        HttpPost httpPost = new HttpPost(builder.build());
        //encoding
        List<String> base64EncodedImages = new ArrayList<>();
        for(String path : paths){
            try {
                base64EncodedImages.add(FileCode.encodeImages(path));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        //to JSON
        String jsonImagesStrings = JSONObject.toJSONString(new jsonPostString(
                "images",
                base64EncodedImages.size(),
                base64EncodedImages
        ));
        //do POST
        httpPost.setEntity(new StringEntity(jsonImagesStrings, ContentType.APPLICATION_JSON));
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            //get statusPOST
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("状态码:"+statusCode);
        } catch (IOException exception) {
            //print error
            exception.printStackTrace();
        }
    }

    public static void doDelete(){

    }

    private static class jsonPostString{
        String name;
        int num;
        List<String> images;
        jsonPostString(String name,int num,List<String> images){
            this.images = images;
            this.name = name;
            this.num = num;
        }
    }
}
