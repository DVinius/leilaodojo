package br.com.vsgdev.leilaodojo.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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
        params.put("deviceId", user.getDeviceId());
        params.put("gcmToken", user.getGcmToken());

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
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
            product.getImgProduto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] bytes = baos.toByteArray();
            final String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
            params.put("image", encoded);
        }

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }
}
