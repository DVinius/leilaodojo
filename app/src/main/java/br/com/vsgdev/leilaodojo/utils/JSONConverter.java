package br.com.vsgdev.leilaodojo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.apache.http.HttpClientConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.vsgdev.leilaodojo.models.Auction;
import br.com.vsgdev.leilaodojo.models.Product;
import br.com.vsgdev.leilaodojo.models.User;

/**
 * Classe que administra as conversoes entre os modelos e os suas representacoes JSON
 */
public class JSONConverter {
    public static String deviceId;

    /**
     * Converte objeto User para JsonObject
     *
     * @param user a instancia de User a ser convertida
     * @return um objeto JsonObject
     */
    public static JSONObject userToJson(final User user) {
        final Map<String, String> params = new HashMap<>();

        params.put("id", String.valueOf(user.getId()));
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        params.put("deviceId", user.getDeviceId());
        params.put("gcmToken", user.getGcmToken());

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }

    public static User responseToUser(final JSONObject response){
        final User user = new User();

        try {
            user.setId(response.getInt("id"));
            user.setDeviceId(response.getString("deviceId"));
            user.setGcmToken(response.getString("gcmToken"));
            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Converte um produto (item em leilao) para objeto JsonObject.
     *
     * @param product a instancia de produto a ser convertida
     * @return jsonObject convertido
     */
    public static JSONObject productToJson(final Product product) {
        final Map<String, String> params = new HashMap<>();

        params.put("id", String.valueOf(product.getId()));
        params.put("name", product.getNomeProduto());
        params.put("description", product.getDescProduto());
        params.put("estimatedValue", String.valueOf(product.getValorEstimado()));
        if (product.getImgProduto() != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            product.getImgProduto().compress(Bitmap.CompressFormat.JPEG, 90, baos);
            final byte[] bytes = baos.toByteArray();
            final String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
            params.put("image", encoded);
        }

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }

    public static Auction responseToAuction(final JSONObject response) {
        final Auction auction = new Auction();
        try {
            auction.setId(response.getInt("id"));
            final Product product = new Product();

            product.setNomeProduto(response.getJSONObject("product").getString("name"));
            product.setDescProduto(response.getJSONObject("product").getString("description"));
            product.setValorEstimado(response.getJSONObject("product").getDouble("estimatedValue"));
            auction.setProduct(product);
            if (!response.getJSONObject("product").getString("image").isEmpty()) {
                //convert image data to bitmap
                final String encodedImage = response.getJSONObject("product").getString("image");
                final byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                product.setImgProduto(bitmap);
            }
            if (response.has("createdAt")){
                auction.setCreatedAt(DateTimeUtils.strToCalendar(response.getString("createdAt")));
            }
            if (response.has("endAt")){
                auction.setEndAt(DateTimeUtils.strToCalendar(response.getString("endAt")));
            }
            if (response.has("bidUser") && !response.getString("bidUser").isEmpty() && !response.getString("bidUser").equals("null")){
                final Object jsonObj = response.get("bidUser");
                if (jsonObj != null){
                    auction.setBidOwner(responseToUser((JSONObject)jsonObj));
                }
            }
            if (response.has("bid") &&  !response.getString("bid").equals("null")){
                auction.setLastBid(response.getDouble("bid"));
            }

            if (response.has("owner") && !response.getString("owner").isEmpty() && !response.getString("owner").equals("null")){
                final Object jsonObj = response.get("owner");
                if (jsonObj != null){
                    auction.setOwner(responseToUser((JSONObject)jsonObj));
                }
            }

            return auction;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject auctionToJson(final Auction auction) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", String.valueOf(auction.getId()));
        if (auction.getProduct() != null) {
            params.put("product", productToJson(auction.getProduct()));
        }
        if (auction.getCreatedAt() != null){
            params.put("createdAt", DateTimeUtils.convertCalendar(auction.getCreatedAt()));
        }
        if (auction.getEndAt() != null){
            params.put("endAt", DateTimeUtils.convertCalendar(auction.getEndAt()));
        }
        if (auction.getOwner() != null) {
            params.put("owner", userToJson(auction.getOwner()));
        }
        if (auction.getBidOwner() != null){
            params.put("bidOwner", userToJson(auction.getBidOwner()));
        }
        params.put("lastBid", auction.getLastBid());

        final JSONObject resultJson = new JSONObject(params);
        return resultJson;
    }

}