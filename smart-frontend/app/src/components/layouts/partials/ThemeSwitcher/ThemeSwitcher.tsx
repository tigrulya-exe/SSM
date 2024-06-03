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
import React, { useEffect } from 'react';
import s from './ThemeSwitcher.module.scss';
import { useLocalStorage } from '@hooks/useLocalStorage';
import cn from 'classnames';
import { IconButton } from '@uikit';

enum THEME {
  Dark = 'dark',
  Light = 'light',
}

const THEMES_CLASS = {
  [THEME.Dark]: 'theme-dark',
  [THEME.Light]: 'theme-light',
};

const THEME_STORAGE_KEY = 'css_theme_name';

const switchToTheme = (theme: THEME) => {
  Object.entries(THEMES_CLASS).forEach(([themeName, className]) => {
    document.body.classList.toggle(className, themeName === theme);
  });
};

const ThemeSwitcher: React.FC = () => {
  const [theme, saveThemeToStorage] = useLocalStorage({ key: THEME_STORAGE_KEY });

  const darkOn = () => {
    saveThemeToStorage(THEME.Dark);
    switchToTheme(THEME.Dark);
  };
  const lightOn = () => {
    saveThemeToStorage(THEME.Light);
    switchToTheme(THEME.Light);
  };

  // save theme to storage for first connect to page
  useEffect(() => {
    if (theme === THEME.Light) {
      lightOn();
    } else {
      darkOn();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className={s.themeSwitcher}>
      <IconButton
        className={cn({ 'is-active': theme === THEME.Light })}
        icon="sun"
        size={28}
        title={theme === THEME.Dark ? 'Switch to light theme' : undefined}
        onClick={lightOn}
        variant="secondary"
      />
      <IconButton
        className={cn({ 'is-active': theme === THEME.Dark })}
        icon="moon"
        size={28}
        title={theme === THEME.Light ? 'Switch to dark theme' : undefined}
        onClick={darkOn}
        variant="secondary"
      />
    </div>
  );
};

export default ThemeSwitcher;
