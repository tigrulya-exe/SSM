import React from 'react';
import TableCell, { type TableCellProps } from '@uikit/Table/TableCell/TableCell';
import ConditionalWrapper from '@uikit/ConditionalWrapper/ConditionalWrapper';
import { Tooltip } from '@uikit';
import s from './BigTextCell.module.scss';
import { orElseGet } from '@utils/checkUtils';

const tenLinesHeight = 197;

const prepareText = (value: string, limit: number) => {
  return value.length > limit ? `${value.substring(0, limit)}...` : value;
};

interface BigTextCellProps extends TableCellProps {
  value?: string | null;
  limit?: number;
}

const BigTextCell: React.FC<BigTextCellProps> = ({ value, width, minWidth = '400px', limit = 100, ...props }) => {
  const text = orElseGet(value, (value) => prepareText(value, limit));

  // if text for tooltip is empty then don't show empty tooltip
  const isEmptyValue = !value?.length;

  const style = { maxWidth: 0, minWidth: width ?? minWidth };

  return (
    <TableCell {...props} style={style}>
      <ConditionalWrapper
        Component={Tooltip}
        isWrap={!isEmptyValue}
        label={value}
        placement="top-start"
        className={s.bigTextCell__tooltip}
      >
        <div className={s.bigTextCell__textWrap} style={{ maxHeight: tenLinesHeight }}>
          {text}
        </div>
      </ConditionalWrapper>
    </TableCell>
  );
};

export default BigTextCell;
