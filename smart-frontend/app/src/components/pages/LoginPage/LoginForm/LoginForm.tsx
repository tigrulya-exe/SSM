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
import React, { useState } from 'react';
import { useDispatch, useStore } from '@hooks';
import { clearError, login } from '@store/authSlice';
import Input from '@uikit/Input/Input';
import InputPassword from '@uikit/InputPassword/InputPassword';
import Button from '@uikit/Button/Button';
import FormField from '@uikit/FormField/FormField';

import s from './LoginForm.module.scss';
import FormFieldsContainer from '@uikit/FormField/FormFieldsContainer';

const LoginForm: React.FC = () => {
  const dispatch = useDispatch();
  const { hasError, message } = useStore((s) => s.auth);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const errorMessage = hasError ? message : undefined;

  const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(clearError());
    setUsername(event.target.value);
  };

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(clearError());
    setPassword(event.target.value);
  };

  const handleSubmit = (event: React.SyntheticEvent) => {
    event.preventDefault();
    dispatch(
      login({
        username,
        password,
      }),
    );
  };
  return (
    <form onSubmit={handleSubmit} autoComplete="off" className={s.loginForm}>
      <div className={s.loginForm__title}>Log In</div>
      <FormFieldsContainer>
        <FormField label="User" error={errorMessage}>
          <Input
            value={username}
            type="text"
            name="username"
            onChange={handleUsernameChange}
            placeholder="Enter username"
            autoComplete="username"
          />
        </FormField>
        <FormField label="Password" hasError={hasError}>
          <InputPassword
            value={password}
            placeholder="Enter password"
            onChange={handlePasswordChange}
            autoComplete="current-password"
          />
        </FormField>

        <Button type="submit" className={s.loginForm__submit} hasError={hasError} disabled={hasError}>
          Sign in
        </Button>
      </FormFieldsContainer>
    </form>
  );
};
export default LoginForm;
