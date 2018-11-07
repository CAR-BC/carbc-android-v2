package core.connection;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import HelperInterface.AsyncResponse;
import core.blockchain.Block;
import core.blockchain.BlockInfo;
import core.consensus.PeerDetail;

public class APICaller extends AsyncTask<Object,String,JSONArray> {

    public AsyncResponse delegate = null;

    @Override
    protected JSONArray doInBackground(Object[] objects) {
        String api_url = (String) objects[0];
        String http_method  =(String)objects[1];
        String objectType = (String )objects[2];
        //BlockInfo blockInfo = (BlockInfo)objects[2];

        try {
            System.out.println(api_url);

            if (http_method.equals("POST")){
                URL url =new URL(api_url);
                HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data = "";
                System.out.println(api_url);

                if (objectType.equals("BlockInfo")){
                    BlockInfo blockInfo = (BlockInfo) objects[3];
                    post_data= URLEncoder.encode("previous_hash","UTF-8")+"="+URLEncoder.encode(blockInfo.getPreviousHash(),"UTF-8")+"&"+
                            URLEncoder.encode("block_hash","UTF-8")+"="+URLEncoder.encode(blockInfo.getHash(),"UTF-8")+"&"+
                            URLEncoder.encode("block_timestamp","UTF-8")+"="+URLEncoder.encode("2018-11-06 23:46:16","UTF-8")+"&"+
                            URLEncoder.encode("block_number","UTF-8")+"="+URLEncoder.encode(Long.toString(blockInfo.getBlockNumber()),"UTF-8")+"&"+
                            URLEncoder.encode("validity","UTF-8")+"="+URLEncoder.encode("1","UTF-8")+"&"+
                            URLEncoder.encode("transaction_id","UTF-8")+"="+URLEncoder.encode(blockInfo.getTransactionId(),"UTF-8")+"&"+
                            URLEncoder.encode("sender","UTF-8")+"="+URLEncoder.encode(blockInfo.getSender(),"UTF-8")+"&"+
                            URLEncoder.encode("event","UTF-8")+"="+URLEncoder.encode(blockInfo.getEvent(),"UTF-8")+"&"+
                            URLEncoder.encode("data","UTF-8")+"="+URLEncoder.encode(blockInfo.getData(),"UTF-8")+"&"+
                            URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(blockInfo.getAddress(),"UTF-8");
                    System.out.println(api_url);
                }
                else if (objectType.equals("Identity")){
                    Identity identity = (Identity) objects[3];
                    post_data = URLEncoder.encode("block_hash","UTF-8")+"="+URLEncoder.encode(identity.getBlock_hash(),"UTF-8")+"&"+
                            URLEncoder.encode("role","UTF-8")+"="+URLEncoder.encode(identity.getRole(),"UTF-8")+"&"+
                            URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(identity.getName(),"UTF-8");
                    System.out.println(api_url);
                }

                else if (objectType.equals("PeerDetail")){
                    PeerDetail peerDetail = (PeerDetail) objects[3];
                    post_data = URLEncoder.encode("peerID","UTF-8")+"="+URLEncoder.encode(peerDetail.getPeerID(),"UTF-8")+"&"+
                            URLEncoder.encode("ip","UTF-8")+"="+URLEncoder.encode(peerDetail.getIp(),"UTF-8")+"&"+
                            URLEncoder.encode("listeningPort","UTF-8")+"="+URLEncoder.encode(String.valueOf(peerDetail.getListeningPort()),"UTF-8")+"&"+
                            URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode(peerDetail.getType(),"UTF-8");

                }

                System.out.println(post_data);
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while ((line=bufferedReader.readLine())!=null){
                    result+=line;

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.d("OUTPUT++",result);
                delegate.processFinish(new JSONArray(result));
                return new JSONArray(result);
            }
            else{

                URL url =new URL(api_url);
                HttpGet httppost = new HttpGet(String.valueOf(url));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONArray jsono = new JSONArray(data);
                    System.out.println(jsono.toString());
                    JSONObject object = new JSONObject();
                    delegate.processFinish(jsono);
                    return jsono;
                }
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray o) {
        System.out.println("before deligate////////////////////////////////////////////////////////");
        super.onPostExecute(o);
        delegate.processFinish(o);
        System.out.println("ater deligate////////////////////////////////////////////////////////////////////////");
    }
}
