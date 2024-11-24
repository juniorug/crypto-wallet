package com.postfinance.cryptowallet.dto;

import com.postfinance.cryptowallet.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse implements Serializable {

    private double total;
    private Asset bestAsset;
    private Asset worstAsset;

}
