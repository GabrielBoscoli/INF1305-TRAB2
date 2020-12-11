/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.List;

import com.owlike.genson.Genson;

@Contract(name = "MusicContract",
    info = @Info(title = "Music contract",
                description = "My Smart Contract",
                version = "0.0.1",
                license =
                        @License(name = "Apache-2.0",
                                url = ""),
                                contact =  @Contact(email = "music@example.com",
                                                name = "music",
                                                url = "http://music.me")))
@Default
public class MusicContract implements ContractInterface {
    public  MusicContract() {

    }

    @Transaction()
    public boolean musicExists(Context ctx, String musicId) {
        byte[] buffer = ctx.getStub().getState(musicId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public void createMusic(Context ctx, String ISWC, String publisher) {
        boolean exists = musicExists(ctx,ISWC);
        if (exists) {
            throw new RuntimeException("The asset "+ISWC+" already exists");
        }
        Music asset = new Music(ISWC, publisher);
        ctx.getStub().putState(ISWC, asset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public Music readMusic(Context ctx, String musicId) {
        boolean exists = musicExists(ctx,musicId);
        if (!exists) {
            throw new RuntimeException("The asset "+musicId+" does not exist");
        }
        Music newAsset = Music.fromJSONString(new String(ctx.getStub().getState(musicId),UTF_8));
        return newAsset;
    }

    @Transaction()
    public void transferMusic(Context ctx, String musicId, String newPublisher) {
        boolean exists = musicExists(ctx,musicId);
        if (!exists) {
            throw new RuntimeException("The asset "+musicId+" does not exist");
        }
        Music asset = Music.fromJSONString(new String(ctx.getStub().getState(musicId),UTF_8));
        Music newAsset = new Music(asset.getIswc(), newPublisher);
        ctx.getStub().putState(musicId, newAsset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public void deleteMusic(Context ctx, String musicId) {
        boolean exists = musicExists(ctx,musicId);
        if (!exists) {
            throw new RuntimeException("The asset "+musicId+" does not exist");
        }
        ctx.getStub().delState(musicId);
    }

    @Transaction()
    public String GetAllMusics(final Context ctx) {
        Genson genson = new Genson();
        ChaincodeStub stub = ctx.getStub();
        List<Music> queryResults = new ArrayList<Music>();
        // stub.getStateByRange("", ""); não está funcionando!
        // atualmente stub.getStateByRange("A", "Z"); dá conta do recado pois todas as ISWC
        // começam com o prefixo T
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("A", "Z");

        for (KeyValue result: results) {
            Music asset = genson.deserialize(result.getStringValue(), Music.class);
            queryResults.add(asset);
            System.out.println(asset.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

}
