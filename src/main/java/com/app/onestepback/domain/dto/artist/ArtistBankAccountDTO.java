package com.app.onestepback.domain.dto.artist;

import com.app.onestepback.domain.type.bank.BankCode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistBankAccountDTO {
    Long artistId;
    private BankCode bankCode;
    private String accountNumber;
    private String accountHolder;
}
