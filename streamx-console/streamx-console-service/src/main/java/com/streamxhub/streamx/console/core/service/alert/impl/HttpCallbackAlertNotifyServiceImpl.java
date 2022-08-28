/*
 * Copyright 2019 The StreamX Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamxhub.streamx.console.core.service.alert.impl;

import com.streamxhub.streamx.console.base.exception.AlertException;
import com.streamxhub.streamx.console.base.util.FreemarkerUtils;
import com.streamxhub.streamx.console.core.bean.AlertConfigWithParams;
import com.streamxhub.streamx.console.core.bean.AlertHttpCallbackParams;
import com.streamxhub.streamx.console.core.bean.AlertTemplate;
import com.streamxhub.streamx.console.core.service.alert.AlertNotifyService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author weijinglun
 * @date 2022.01.14
 */
@Slf4j
@Service
@Lazy
public class HttpCallbackAlertNotifyServiceImpl implements AlertNotifyService {
    @Autowired
    private RestTemplate alertRestTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean doAlert(AlertConfigWithParams alertConfig, AlertTemplate alertTemplate) throws AlertException {
        AlertHttpCallbackParams alertHttpCallbackParams = alertConfig.getHttpCallbackParams();

        String requestTemplate = alertHttpCallbackParams.getRequestTemplate();
        if (!StringUtils.hasLength(requestTemplate)) {
            return false;
        }
        try {
            Template template = FreemarkerUtils.loadTemplateString(requestTemplate);
            String format = FreemarkerUtils.format(template, alertTemplate);
            Map<String, Object> body = mapper.readValue(format, new TypeReference<Map<String, Object>>() {
            });
            sendMessage(alertHttpCallbackParams, body);
            return true;
        } catch (AlertException alertException) {
            throw alertException;
        } catch (Exception e) {
            throw new AlertException("Failed send httpCallback alert", e);
        }
    }

    private Object sendMessage(AlertHttpCallbackParams params, Map<String, Object> body) throws AlertException {
        String url = params.getUrl();
        HttpHeaders headers = new HttpHeaders();
        String contentType = params.getContentType();
        MediaType mediaType = MediaType.APPLICATION_JSON;
        if (StringUtils.hasLength(contentType)) {
            switch (contentType.toLowerCase()) {
                case MediaType.APPLICATION_FORM_URLENCODED_VALUE:
                    mediaType = MediaType.APPLICATION_FORM_URLENCODED;
                    break;
                case MediaType.MULTIPART_FORM_DATA_VALUE:
                    mediaType = MediaType.MULTIPART_FORM_DATA;
                    break;
                case MediaType.APPLICATION_JSON_VALUE:
                default:
                    break;
            }
        }
        headers.setContentType(mediaType);

        ResponseEntity<Object> response;
        try {
            HttpMethod httpMethod = HttpMethod.POST;
            String method = params.getMethod();
            if (!StringUtils.hasLength(method)) {
                if (HttpMethod.PUT.name().equalsIgnoreCase(method)) {
                    httpMethod = HttpMethod.PUT;
                }
            }
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RequestCallback requestCallback = alertRestTemplate.httpEntityCallback(entity, Object.class);
            ResponseExtractor<ResponseEntity<Object>> responseExtractor = alertRestTemplate.responseEntityExtractor(Object.class);
            response = alertRestTemplate.execute(url, httpMethod, requestCallback, responseExtractor);
        } catch (Exception e) {
            log.error("Failed to request httpCallback alert,\nurl:{}", url, e);
            throw new AlertException(String.format("Failed to request httpCallback alert,\nurl:%s", url), e);
        }

        if (response == null) {
            throw new AlertException(String.format("Failed to request httpCallback alert,\nurl:%s", url));
        }

        return response;
    }
}
