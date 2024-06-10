/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React from 'react';
import { Link } from 'react-router-dom';
import s from './NotFoundPage.module.scss';
import ErrorTextContainer from '@commonComponents/ErrorPageContent/ErrorTextContainer/ErrorTextContainer';
import ErrorPageContent from '@commonComponents/ErrorPageContent/ErrorPageContent';

const NotFoundPage = () => {
  return (
    <div className={s.notFoundPage}>
      <ErrorPageContent errorCode="404">
        <ErrorTextContainer errorHeader="Page not found">
          <div>Page you’re trying to reach doesn’t exist or was removed</div>
          <div className={s.notFoundPage__link}>
            Please return to{' '}
            <Link className="text-link" to="/">
              Dashboard
            </Link>{' '}
            page
          </div>
        </ErrorTextContainer>
      </ErrorPageContent>
    </div>
  );
};

export default NotFoundPage;
