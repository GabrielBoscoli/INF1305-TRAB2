/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class Music {

    private final static Genson genson = new Genson();

    @Property()
    private final String iswc;

    @Property()
    private final String publisher;

    public Music(@JsonProperty("iswc") final String iswc, @JsonProperty("publisher") String publisher) {
        this.iswc = iswc;
        this.publisher = publisher;
    }

    public String getIswc() {
        return this.iswc;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String toJSONString() {
        return genson.serialize(this).toString();
    }

    public static Music fromJSONString(String json) {
        Music asset = genson.deserialize(json, Music.class);
        return asset;
    }
}
