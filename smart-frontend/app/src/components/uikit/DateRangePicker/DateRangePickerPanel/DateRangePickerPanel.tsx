import React, { useState } from 'react';
import TabsBlock from '@uikit/Tabs/TabsBlock';
import TabButton from '@uikit/Tabs/TabButton';
import RangesTab from '../Tabs/RangesTab';
import CalendarTab from '../Tabs/CalendarTab';
import type { RangePreset } from '../DateRangePicker.types';
import { defaultRangesPreset } from '../DateRangePicker.constants';
import { type DateRange, type DynamicDateRange, isDynamicDateRange, isStaticDateRange } from '@models/dateRange';

type DatePickerTabs = 'range' | 'calendar';

export interface DateRangePickerPanelProps {
  range?: DateRange;
  onApply: (range: DateRange) => void;
  onRevert: () => void;
  rangesPreset?: RangePreset[];
}

const DateRangePickerPanel: React.FC<DateRangePickerPanelProps> = ({
  range,
  onApply,
  onRevert,
  rangesPreset = defaultRangesPreset,
}) => {
  const [activeTab, setActiveTab] = useState<DatePickerTabs>(() => {
    return isStaticDateRange(range) ? 'calendar' : 'range';
  });

  const getHandleTabClick = (tabName: DatePickerTabs) => () => {
    setActiveTab(tabName);
  };

  const handleApplyDynamicRange = (range: DynamicDateRange) => {
    onApply(range);
  };

  const handleApplyStaticDateRange = (from: Date, to: Date) => {
    onApply({ from, to });
  };

  return (
    <div data-test="data-picker-panel">
      <TabsBlock>
        <TabButton onClick={getHandleTabClick('range')} isActive={activeTab === 'range'}>
          Range
        </TabButton>
        <TabButton onClick={getHandleTabClick('calendar')} isActive={activeTab === 'calendar'}>
          Calendar
        </TabButton>
      </TabsBlock>
      {activeTab === 'range' && (
        <RangesTab
          range={isDynamicDateRange(range) ? range : undefined}
          onApply={handleApplyDynamicRange}
          onRevert={onRevert}
          rangesPreset={rangesPreset}
        />
      )}
      {activeTab === 'calendar' && (
        <CalendarTab
          rangeFrom={isStaticDateRange(range) ? range.from : undefined}
          rangeTo={isStaticDateRange(range) ? range.to : undefined}
          onApply={handleApplyStaticDateRange}
          onRevert={onRevert}
        />
      )}
    </div>
  );
};

export default DateRangePickerPanel;
