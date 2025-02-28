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

package org.apache.streampark.console.core.service.application;

import org.apache.streampark.console.base.exception.ApplicationException;
import org.apache.streampark.console.core.entity.SparkApplication;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * This interface represents an Application Operation Service. It extends the IService interface for
 * handling Application entities.
 */
public interface SparkApplicationActionService extends IService<SparkApplication> {

    /**
     * Starts the specified application.
     *
     * @param appParam The application to start.
     * @param auto True if the application should start automatically, False otherwise.
     * @throws Exception If an error occurs while starting the application.
     */
    void start(SparkApplication appParam, boolean auto) throws Exception;

    /**
     * Restarts the given application.
     *
     * @param appParam The application to restart.
     * @throws Exception If an error occurs while restarting the application.
     */
    void restart(SparkApplication appParam) throws Exception;

    /**
     * Revokes access for the given application.
     *
     * @param appId The application's id for which access needs to be revoked.
     * @throws ApplicationException if an error occurs while revoking access.
     */
    void revoke(Long appId) throws ApplicationException;

    /**
     * Stop the given application. Throws an exception if stop fails.
     *
     * @param appParam the application to be stopped
     * @throws Exception if stop fails
     */
    void cancel(SparkApplication appParam) throws Exception;

    /**
     * Forces the given application to stop.
     *
     * @param id the application's id which need to be stopped
     */
    void forcedStop(Long id);

    /**
     * Get the application by cluster id.
     * @param clusterId
     * @return
     */
    List<SparkApplication> getByClusterId(String clusterId);

}
