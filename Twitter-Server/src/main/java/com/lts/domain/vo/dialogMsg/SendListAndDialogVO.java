package com.lts.domain.vo.dialogMsg;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor(staticName = "of")
public class SendListAndDialogVO {
    private List<DialogListVO> dialogListVOList;
    private List<DialogVO> dialogVOList;
}
