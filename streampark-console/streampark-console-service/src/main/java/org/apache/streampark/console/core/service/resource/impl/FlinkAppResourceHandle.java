/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.core.service.resource.impl;

import org.apache.streampark.common.Constant;
import org.apache.streampark.common.util.Utils;
import org.apache.streampark.console.base.domain.RestResponse;
import org.apache.streampark.console.base.exception.ApiAlertException;
import org.apache.streampark.console.core.entity.Resource;
import org.apache.streampark.console.core.service.ResourceService;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FlinkAppResourceHandle extends AbstractResourceHandle {

  public FlinkAppResourceHandle(ResourceService resourceService) {
    super(resourceService);
  }

  @Override
  public RestResponse checkResource(Resource resourceParam) {
    // check main.
    File jarFile;
    try {
      jarFile = getResourceJar(resourceParam);
    } catch (Exception e) {
      // get jarFile error
      return buildExceptResponse(e, 1);
    }
    ApiAlertException.throwIfTrue(jarFile == null, "flink app jar must exist.");
    Map<String, Serializable> resp = new HashMap<>(0);
    resp.put(STATE, 0);
    if (jarFile.getName().endsWith(Constant.PYTHON_SUFFIX)) {
      return RestResponse.success().data(resp);
    }
    String mainClass = Utils.getJarManClass(jarFile);
    if (mainClass == null) {
      // main class is null
      return buildExceptResponse(new RuntimeException("main class is null"), 2);
    }
    return RestResponse.success().data(resp);
  }
}
