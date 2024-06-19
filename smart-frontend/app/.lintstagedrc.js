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
import { ESLint } from 'eslint'

const eslintCheck = (filenames) => `eslint ${filenames.join(' ')} --config ./.eslintrc.full.json --ext ts,tsx --report-unused-disable-directives --max-warnings 0`;

/**
 * lint-stage don't understand .eslintignore file
 * https://www.curiouslychase.com/posts/eslint-error-file-ignored-because-of-a-matching-ignore-pattern/
 */
const removeIgnoredFiles = async (files) => {
  const eslint = new ESLint()
  const isIgnored = await Promise.all(
    files.map((file) => {
      return eslint.isPathIgnored(file)
    })
  )
  return files.filter((_, i) => !isIgnored[i]);
}

export default {
  '*.(js|jsx|ts|tsx)': async (filenames) => {
    // Run ESLint on entire repo if more than 10 staged files
    if (filenames.length > 10) {
      return 'yarn lint'
    }
    const filesToLint = await removeIgnoredFiles(filenames);
    return eslintCheck(filesToLint);
  }
}
