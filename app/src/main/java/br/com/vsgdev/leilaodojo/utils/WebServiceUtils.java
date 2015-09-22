package br.com.vsgdev.leilaodojo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.com.vsgdev.leilaodojo.models.Product;
import br.com.vsgdev.leilaodojo.models.User;

/**
 * Esta classe administra os serviços web utilizados pelo app.
 */
public class WebServiceUtils {
    public static boolean production = false;
    public static final String PORT = "9000";
    public static RequestQueue queue;

    public static final String WS_REGISTER_USER = "/registerUser";

    private static String getServer() {
        if (production) {
            return "http://107.170.121.53";
        } else {
            return "http://192.168.0.49";
        }
    }

    private static RequestQueue getRequestQueue(final Context context) {
        if (queue == null && context != null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public static void registerUser(final User user, final Context context) {
        final JSONObject jsonUser = JSONConverter.userToJson(user);

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getServer() + ":" + PORT + WS_REGISTER_USER,
                jsonUser,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("WebService", "user register successful");
                            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("WebService", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError on register user. " + error.toString());
            }
        }
        );
        getRequestQueue(context).add(request);
    }

    /**
     * Cria uma oferta para leilao
     *
     * @param product  o produto a ser leilado
     * @param activity atividade/contexto para retorno/feedback visual
     */
    public static void offerProuct(final Product product, final Activity activity) {
        final JSONObject jsonProduct = JSONConverter.productToJson(product);
        //TODO XXX Continue
    }
}