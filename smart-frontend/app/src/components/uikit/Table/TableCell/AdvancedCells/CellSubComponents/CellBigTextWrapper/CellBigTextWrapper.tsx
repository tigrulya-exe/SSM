import React from 'react';
import s from './CellBigTextWrapper.module.scss';
import { orElseGet } from '@utils/checkUtils';
import { prepareText } from './CellBigTextWrapper.utils';

const tenLinesHeight = 197;

export interface CellBigTextWrapperProps {
  text: string;
  textLimit?: number;
  maxHeight?: number;
}

const CellBigTextWrapper = ({ text, maxHeight = tenLinesHeight, textLimit = 100 }: CellBigTextWrapperProps) => {
  const preparedText = orElseGet(text, (value) => prepareText(value, textLimit));

  return (
    <div className={s.cellBigTextWrapper} style={{ maxHeight: maxHeight }}>
      {preparedText}
    </div>
  );
};

export default CellBigTextWrapper;
