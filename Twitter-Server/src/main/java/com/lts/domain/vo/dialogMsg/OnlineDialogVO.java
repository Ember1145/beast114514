package com.lts.domain.vo.dialogMsg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class OnlineDialogVO {
    private DialogVO dialogVO;
    private String type;
}
