import type { ForwardedRef } from 'react';
import { useRef, useState, forwardRef } from 'react';
import IconButton from '../IconButton/IconButton';
import { formatDate } from './DateRangePicker.utils';
import s from './DateRangePicker.module.scss';
import cn from 'classnames';
import { useForwardRef } from '@hooks/useForwardRef';
import Popover from '@uikit/Popover/Popover';
import PopoverPanelDefault from '@uikit/Popover/PopoverPanelDefault/PopoverPanelDefault';
import Input from '@uikit/Input/Input';
import type { FieldProps } from '@uikit/Field/Field.types';
import DateRangePickerPanel from './DateRangePickerPanel/DateRangePickerPanel';
import { defaultRangesPreset } from './DateRangePicker.constants';
import type { RangePreset } from './DateRangePicker.types';
import type { DateRange } from '@models/dateRange';
import { isDynamicDateRange, isStaticDateRange } from '@models/dateRange';

export interface DateRangePickerProps extends FieldProps {
  placeholder?: string;
  range?: DateRange;
  className?: string;
  containerRef?: React.Ref<HTMLDivElement>;
  onApply: (range: DateRange) => void;
  rangesPreset?: RangePreset[];
}

const DateRangePicker = forwardRef(
  (
    {
      placeholder,
      range,
      onApply,
      disabled = false,
      className,
      containerRef,
      hasError = false,
      rangesPreset = defaultRangesPreset,
    }: DateRangePickerProps,
    ref: ForwardedRef<HTMLInputElement>,
  ) => {
    const [open, setOpen] = useState(false);

    const localContainerRef = useRef(null);
    const containerReference = useForwardRef(localContainerRef, containerRef);

    const handleToggle = () => {
      !disabled && setOpen((isOpen) => !isOpen);
    };

    const handleApply = (range: DateRange) => {
      onApply(range);
      handleToggle();
    };

    const handleRevert = () => {
      handleToggle();
    };

    const inputClassNames = cn(s.dateRangePicker__input, className, {
      'is-active': open,
      [s.dateRangePicker__input_long]: isStaticDateRange(range),
    });

    const formattedRange =
      range !== undefined
        ? isDynamicDateRange(range)
          ? range
          : `${formatDate(range?.from)} - ${formatDate(range?.to)}`
        : '';

    return (
      <>
        <Input
          ref={ref}
          containerRef={containerReference}
          value={formattedRange}
          readOnly
          disabled={disabled}
          className={inputClassNames}
          placeholder={placeholder}
          hasError={hasError}
          onClick={handleToggle}
          endAdornment={<IconButton icon="chevron" onClick={handleToggle} size={12} variant="secondary" />}
        />
        <Popover
          isOpen={open}
          onOpenChange={setOpen}
          triggerRef={localContainerRef}
          placement="bottom-end"
          offset={{
            alignmentAxis: -11,
            mainAxis: 16,
          }}
        >
          <PopoverPanelDefault>
            <DateRangePickerPanel
              range={range}
              onApply={handleApply}
              onRevert={handleRevert}
              rangesPreset={rangesPreset}
            />
          </PopoverPanelDefault>
        </Popover>
      </>
    );
  },
);

export default DateRangePicker;
