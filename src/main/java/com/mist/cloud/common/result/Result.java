package com.mist.cloud.common.result;

import com.mist.cloud.common.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: securemist
 * @Datetime: 2023/7/18 15:20
 * @Description:
 */
public interface Result extends Serializable {
     long serialVersionUID = -3826891916021780628L;
}
