package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

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
/**
 * @Date 2020/4/26
 * @Author Hi lu
 */
public class HttpUtil {
    public static final CloseableHttpClient  client = HttpClientBuilder.create().build();
    @Getter
    private static final String URI = "http://139.199.66.139:8080/myImages";//硬编程

    /**
     * do the http get method to request which of images divided into page
     * @param page
     * @param size
     * @throws URISyntaxException
     */
    public static void doGetPageImages(int page, int size) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(URI + "/getImagesDivideIntoPages");
        //set the params of PAGE
        List<NameValuePair> params= new ArrayList<>();
        params.add(new BasicNameValuePair("page",String.valueOf(page)));
        params.add(new BasicNameValuePair("size",String.valueOf(size)));
        builder.addParameters(params);
        HttpGet httpGet = new HttpGet(builder.build());
        TaskThreadPools.execute(()->{
            try {
                //do GET
                CloseableHttpResponse response = client.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("GET状态码:"+statusCode);
                HttpEntity entity = response.getEntity();
                String jsonImagesStrings = EntityUtils.toString(entity, "utf-8");
                System.out.println(jsonImagesStrings);
                //to IMAGES
                JSONArray imagesArray = JSONArray.parseArray(jsonImagesStrings);
                for(Object o : imagesArray){
                    JSONObject image = (JSONObject) o;
                    FileCode.decodeBASE64(image.getString("base64EncodedImag"),
                            System.getProperty("user.dir")+"/cloudAlbum");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * do the  http post method to save the images to database
     * @param paths
     * @throws URISyntaxException
     */
    public static void doPostJson(String[] paths) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(URI + "/addImages");
        HttpPost httpPost = new HttpPost(builder.build());
        TaskThreadPools.execute(()->{
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
        });
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
