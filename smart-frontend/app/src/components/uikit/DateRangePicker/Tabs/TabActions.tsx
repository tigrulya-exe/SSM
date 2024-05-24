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
import Button from '@uikit/Button/Button';
import ts from './Tabs.module.scss';

export interface TabActionsProps {
  isApplyDisable?: boolean;
  onApply: () => void;
  onRevert: () => void;
}

const TabActions = ({ isApplyDisable = false, onApply, onRevert }: TabActionsProps) => (
  <div className={ts.dateRangePickerTab__rightFooterButtons}>
    <Button size="small" variant="secondary" onClick={onRevert}>
      Revert
    </Button>
    <Button disabled={isApplyDisable} size="small" onClick={onApply}>
      Apply
    </Button>
  </div>
);

export default TabActions;
