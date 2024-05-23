import { useState } from 'react';
import TabActions from './TabActions';
import ts from './Tabs.module.scss';
import s from './RangesTab.module.scss';
import cn from 'classnames';
import FormField from '@uikit/FormField/FormField';
import Input from '@uikit/Input/Input';
import type { RangePreset } from '../DateRangePicker.types';
import type { DynamicDateRange } from '@models/dateRange';

interface RangesTabProps {
  range?: DynamicDateRange;
  onApply: (range: DynamicDateRange) => void;
  onRevert: () => void;
  rangesPreset: RangePreset[];
}

const RangesTab = ({ range, onApply, onRevert, rangesPreset }: RangesTabProps) => {
  const [selectedRangeId, setSelectedRangeId] = useState<DynamicDateRange | undefined>(range);

  const handleClick = (item: RangePreset) => {
    setSelectedRangeId(item.id);
  };

  const handleApply = () => {
    selectedRangeId && onApply(selectedRangeId);
  };

  return (
    <div className={ts.dateRangePickerTab}>
      <div className={cn(ts.dateRangePickerTab__left, s.rangesPreset, 'scroll')}>
        {rangesPreset.map((item) => {
          const className = cn(s.rangesPreset__buttons, {
            [s.rangesPreset__buttons_selected]: item.id === selectedRangeId,
          });

          return (
            <button tabIndex={-1} key={item.id} className={className} onClick={() => handleClick(item)}>
              {item.description}
            </button>
          );
        })}
      </div>
      <div className={ts.dateRangePickerTab__right}>
        <div className={ts.dateRangePickerTab__rightInputs}>
          <FormField label="From">
            <Input size="small" value={selectedRangeId ?? ''} readOnly />
          </FormField>
          <FormField label="To">
            <Input size="small" value={selectedRangeId ? 'now' : ''} readOnly disabled={!!selectedRangeId} />
          </FormField>
        </div>
        <TabActions onApply={handleApply} onRevert={onRevert} />
      </div>
    </div>
  );
};

export default RangesTab;
