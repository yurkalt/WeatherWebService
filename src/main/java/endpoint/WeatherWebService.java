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

    public WeatherWebService() {
        this.temperatureKelvin = getTempFromJson();;
    }

    @GET
    @Produces("application/json")
    public Response showInKelvin() throws JSONException, URISyntaxException, IOException {

        String json = fromJsonToString("http://api.openweathermap.org/data/2.5/weather?id=703448&appid=6fb8199d11676c9a54c773d3687bada8");


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("kelvinKiev", temperatureKelvin );

        return Response.status(200).entity(jsonObject.toString()).build();
    }



    @Path("/fahrenheit")
    @GET
    @Produces("application/json")
    public Response fahrenheitTemperature() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        DecimalFormat df = new DecimalFormat("###.##");
        double fahrenheitKiev  = 9/5 * (temperatureKelvin - 273) + 32;
        jsonObject.put("fahrenheitKiev", df.format(fahrenheitKiev));
        return Response.status(200).entity(jsonObject.toString()).build();
    }

    @Path("/celsius")
    @GET
    @Produces("application/json")
    public Response celsiusTemperature() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        DecimalFormat df = new DecimalFormat("###.##");
        double celsiusKiev  = temperatureKelvin - 273;
        jsonObject.put("celsiusKiev", df.format(celsiusKiev));
        return Response.status(200).entity(jsonObject.toString()).build();
    }


    private String fromJsonToString(String link) throws URISyntaxException, IOException {
        URI uri = new URI(link);
        URL url = uri.toURL();
        InputStream is = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        return line;

    }

    private double getTempFromJson() {
        String pattern = Pattern.quote("\"temp\":") + "(.*?)" + Pattern.quote(",\"pressure");

        Pattern patterObj = Pattern.compile(pattern);

        Matcher match = patterObj.matcher("{\"coord\":{\"lon\":30.52,\"lat\":50.43},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":264.49,\"pressure\":1024,\"humidity\":85,\"temp_min\":264.15,\"temp_max\":265.15},\"visibility\":10000,\"wind\":{\"speed\":3,\"deg\":270},\"clouds\":{\"all\":0},\"dt\":1480492800,\"sys\":{\"type\":1,\"id\":7358,\"message\":0.0026,\"country\":\"UA\",\"sunrise\":1480484157,\"sunset\":1480514243},\"id\":703448,\"name\":\"Kiev\",\"cod\":200}");

        if (match.find()) {
            return Double.parseDouble(match.group(1));
        }
        return -1;
    }

}

