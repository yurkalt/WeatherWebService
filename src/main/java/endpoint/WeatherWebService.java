package endpoint;

import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Iurii on 29.11.2016.
 */
@Path("/weather")
public class WeatherWebService {
    private double temperatureKelvin;
    private DecimalFormat df = new DecimalFormat("###.##");;

    {
        try {
            String json = fromJsonToString("http://api.openweathermap.org/data/2.5/weather?id=703448&appid=6fb8199d11676c9a54c773d3687bada8");
            temperatureKelvin = getTempFromJson(json);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces("application/json")
    public Response showInKelvin() throws JSONException, URISyntaxException, IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kelvinKiev", temperatureKelvin );
        return Response.status(200).entity(jsonObject.toString()).build();
    }

    @Path("/fahrenheit")
    @GET
    @Produces("application/json")
    public Response fahrenheitTemperature() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        double fahrenheitKiev  = 9/5 * (temperatureKelvin - 273) + 32;
        jsonObject.put("fahrenheitKiev", df.format(fahrenheitKiev));
        return Response.status(200).entity(jsonObject.toString()).build();
    }

    @Path("/celsius")
    @GET
    @Produces("application/json")
    public Response celsiusTemperature() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        double celsiusKiev  = temperatureKelvin - 273;
        jsonObject.put("celsiusKiev", df.format(celsiusKiev));
        return Response.status(200).entity(jsonObject.toString()).build();
    }


    private String fromJsonToString(String link) throws URISyntaxException, IOException {
        URL url = new URL(link);
        InputStream is = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        return sb.toString();

    }

    private double getTempFromJson(String json) {
        String pattern = Pattern.quote("\"temp\":") + "(.*?)" + Pattern.quote(",\"pressure");
        Pattern patterObj = Pattern.compile(pattern);
        Matcher match = patterObj.matcher(json);
        if (match.find()) {
            return Double.parseDouble(match.group(1));
        }
        return -1;
    }
}

