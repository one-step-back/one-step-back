package com.app.onestepback.domain.dto.payment;

import com.app.onestepback.domain.model.PaymentMethodVO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private String last4Digit;
    private String cardName;
    private boolean isDefault;

    public static PaymentMethodDTO from(PaymentMethodVO vo) {
        if (vo == null) return null;

        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setId(vo.getId());
        dto.setLast4Digit(vo.getLast4Digit());
        dto.setCardName(vo.getCardName());
        dto.setDefault(vo.isDefault());

        return dto;
    }
}
