import CalendarCell from '../CalendarCell/CalendarCell';
import type { CalendarMap } from '../Calendar.types';
import s from './CalendarGrid.module.scss';

interface CalendarGridProps {
  calendarMap: CalendarMap;
  selectedDate?: Date;
  selectedMonth: Date;
  rangeFrom?: Date;
  rangeTo?: Date;
  onDateClick: (date: Date) => void;
}

const CalendarGrid = ({
  calendarMap,
  selectedDate,
  selectedMonth,
  rangeFrom,
  rangeTo,
  onDateClick,
}: CalendarGridProps) => {
  const daysIds: Record<string, Date> = {};

  const handleDayClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    if (e.currentTarget.dataset.dayId) {
      const date: Date = daysIds[e.currentTarget.dataset.dayId];
      onDateClick(date);
    }
  };

  return (
    <div className={s.calendarGrid}>
      {calendarMap.map((week, index) =>
        week.map((day) => {
          const dayId = `${index}${day}`;
          daysIds[dayId] = day;

          return (
            <CalendarCell
              dayId={dayId}
              key={dayId}
              day={day}
              selectedDate={selectedDate}
              selectedMonth={selectedMonth}
              startDate={rangeFrom}
              endDate={rangeTo}
              onClick={handleDayClick}
            />
          );
        }),
      )}
    </div>
  );
};

export default CalendarGrid;
