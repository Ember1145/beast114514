package com.lts.domain.vo.dialogMsg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class OnlineDialogListVO {
    private DialogListVO sendListVO;
    private String dialogCount;
    private String type;
}
