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
import { FlexGroup, IconButton } from '@uikit';
import TableCell from '@uikit/Table/TableCell/TableCell';
import { useDispatch } from '@hooks';
import type { AdhRule } from '@models/adh';
import { openDeleteRuleDialog, openStartRuleDialog, openStopRuleDialog } from '@store/adh/rules/rulesActionsSlice';

interface RuleActionsCellProps {
  rule: AdhRule;
}

const RuleActionsCell: React.FC<RuleActionsCellProps> = ({ rule }) => {
  const dispatch = useDispatch();

  const handlePlay = () => {
    dispatch(openStartRuleDialog(rule));
  };
  const handlePause = () => {
    dispatch(openStopRuleDialog(rule));
  };
  const handleDelete = () => {
    dispatch(openDeleteRuleDialog(rule));
  };

  return (
    <TableCell align="center" data-qa="actions">
      <FlexGroup gap="4px">
        {rule.state === 'ACTIVE' ? (
          <IconButton icon="pause" title="Stop rule" onClick={handlePause} data-qa="action-stop" />
        ) : (
          <IconButton icon="play" title="Start rule" onClick={handlePlay} data-qa="action-start" />
        )}
        <IconButton icon="delete" title="Delete rule" onClick={handleDelete} data-qa="action-delete" />
      </FlexGroup>
    </TableCell>
  );
};

export default RuleActionsCell;
