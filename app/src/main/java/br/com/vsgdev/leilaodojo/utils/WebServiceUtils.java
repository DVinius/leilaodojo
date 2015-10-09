package br.com.vsgdev.leilaodojo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.com.vsgdev.leilaodojo.activities.Adapter;
import br.com.vsgdev.leilaodojo.activities.MainActivity;
import br.com.vsgdev.leilaodojo.activities.NewProduct;
import br.com.vsgdev.leilaodojo.models.Auction;
import br.com.vsgdev.leilaodojo.models.Product;
import br.com.vsgdev.leilaodojo.models.User;

/**
 * Esta classe administra os serviços web utilizados pelo app.
 */
public class WebServiceUtils {
    public static boolean production = true;
    public static final String PORT = "9001";
    public static RequestQueue queue;
    private static boolean requestingNextAuction;

    //registra o usuario em nosso server, armazenando, por exemplo o token GCM.
    public static final String WS_REGISTER_USER = "/registerUser";
    //registra um novo produto para leilao.
    public static final String WS_REGISTER_AUCTION = "/registerAuction";
    //coleta o proximo item em leilao, com base em uma referencia.
    public static final String WS_GET_NEXT_AUCTION = "/getNextAuction";
    //make a bid
    public static final String WS_MAKE_BID = "/makeBid";

    private static String getServer() {
        if (production) {
            return "http://107.170.121.53";
        } else {
            return "http://192.168.0.31";
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
                            final User user = JSONConverter.responseToUser(response);
                            MainActivity.currentUser = user;
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
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(request);
    }


    /**
     * Cria uma oferta para leilao
     *
     * @param auction  objeto que representa um leilao de produto
     * @param activity atividade/contexto para retorno/feedback visual
     */
    public static void registerAuction(final Auction auction, final Activity activity) {
        //converte o produto a ser leiloado em objeto JSON
        final JSONObject jsonAuction = JSONConverter.auctionToJson(auction);
        //cria requisicao ao servico web responsável por colocar um item para leilao (consome web service)
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getServer() + ":" + PORT + WS_REGISTER_AUCTION,
                jsonAuction,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Toast.makeText(activity, "Seu produto foi cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((NewProduct) activity).releaseCamps(true);
                            }
                        });

                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError on registerAuction " + error.toString());
                Toast.makeText(activity, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((NewProduct) activity).releaseCamps(true);
                    }
                });
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //coloca a requisicao ao serviço na fila de requisicoes
        getRequestQueue(activity).add(request);
    }

    /**
     * Busca o proximo item em leilao, com base na referencia fornecida. Este mecanismo permite que o
     * processo seja interrompido a qualquer instante, bem como proporciona economia no plano de dados
     * do usuario.
     *
     * @param context Context da aplicacao, necessaria para possivel coleta do RequestQueue.
     */
    public static void getNextAuction(final Adapter adapter, final Activity context) {
        if (requestingNextAuction) {
            return;
        }
        requestingNextAuction = true;
        Auction reference = new Auction();
        if (adapter.getLast() != null) {
            reference = adapter.getLast();
        } else {
            reference.setId(0);
            reference.setOwner(new User());
        }
        final JSONObject jsonProduct = JSONConverter.auctionToJson(reference);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getServer() + ":" + PORT + WS_GET_NEXT_AUCTION,
                jsonProduct,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("get next auction", response.toString());
                            final Auction auction = JSONConverter.responseToAuction(response);
                            if (auction != null && auction.getId() != 0) {
                                adapter.addItem(auction);
                                adapter.notifyDataSetChanged();
                                requestingNextAuction = false;
                                getNextAuction(adapter, context);
                            } else {
                                requestingNextAuction = false;
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.pbLoadItens.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("WebService", e.getMessage());
                            requestingNextAuction = false;
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError on getNextAuction " + error.toString());
                requestingNextAuction = false;
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //coloca a requisicao ao serviço na fila de requisicoes
        getRequestQueue(context).add(request);
    }

    public static void makeABid(final Auction auction, final Activity activity){
        auction.getProduct().setImgProduto(null);
        final JSONObject jsonObject = JSONConverter.auctionToJson(auction);

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                getServer() + ":" + PORT + WS_MAKE_BID,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            final Auction auction = JSONConverter.responseToAuction(response);

                            if (auction.getBidOwner().getDeviceId().equals(JSONConverter.deviceId)){
                                //I am the owner
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("WebService", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError on make a bid " + error.toString());
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(12000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //coloca a requisicao ao serviço na fila de requisicoes
        getRequestQueue(activity).add(request);

    }
}