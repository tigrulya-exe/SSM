import { useState } from 'react';
import Calendar from '@uikit/Calendar/Calendar';
import { endOfDay, isBefore, startOfDay } from '@utils/date/calendarUtils';
import TabActions from './TabActions';
import ts from './Tabs.module.scss';
import FormField from '@uikit/FormField/FormField';
import InputDate from '@uikit/InputDate/InputDate';

interface CalendarTabProps {
  rangeFrom?: Date;
  rangeTo?: Date;
  onApply: (rangeFrom: Date, rangeTo: Date) => void;
  onRevert: () => void;
}

const CalendarTab = ({ rangeFrom, rangeTo, onApply, onRevert }: CalendarTabProps) => {
  const [localDate, setLocalDate] = useState<Date | undefined>(undefined);
  const [localRangeFrom, setLocalRangeFrom] = useState<Date | undefined>(rangeFrom);
  const [localRangeTo, setLocalRangeTo] = useState<Date | undefined>(rangeTo);

  const handleDateClick = (date: Date) => {
    setLocalDate(date);

    if (localRangeFrom === undefined) {
      setLocalRangeFrom(startOfDay(date));
      return;
    }

    if (localRangeFrom !== undefined && localRangeTo === undefined) {
      if (isBefore(date, localRangeFrom)) {
        setLocalRangeTo(localRangeFrom);
        setLocalRangeFrom(startOfDay(date));
      } else {
        setLocalRangeTo(endOfDay(date));
      }
      return;
    }

    if (localRangeFrom !== undefined && localRangeTo !== undefined) {
      setLocalRangeFrom(startOfDay(date));
      setLocalRangeTo(undefined);
      return;
    }
  };

  const handleApply = () => {
    localRangeFrom && localRangeTo && onApply(localRangeFrom, localRangeTo);
  };

  return (
    <div className={ts.dateRangePickerTab}>
      <div className={ts.dateRangePickerTab__left}>
        <Calendar onDateClick={handleDateClick} date={localDate} rangeFrom={localRangeFrom} rangeTo={localRangeTo} />
      </div>
      <div className={ts.dateRangePickerTab__right}>
        <div className={ts.dateRangePickerTab__rightInputs}>
          <FormField label="From">
            <InputDate tabIndex={1} value={localRangeFrom} onChange={setLocalRangeFrom} />
          </FormField>
          <FormField label="To">
            <InputDate tabIndex={2} value={localRangeTo} onChange={setLocalRangeTo} />
          </FormField>
        </div>
        <TabActions
          isApplyDisable={localRangeFrom === undefined || localRangeTo === undefined}
          onApply={handleApply}
          onRevert={onRevert}
        />
      </div>
    </div>
  );
};

export default CalendarTab;
