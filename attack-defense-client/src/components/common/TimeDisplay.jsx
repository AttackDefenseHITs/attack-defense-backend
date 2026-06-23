import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';
import utc from 'dayjs/plugin/utc';
import 'dayjs/locale/ru';

dayjs.extend(duration);
dayjs.extend(utc);

const TimeDisplay = ({ label, timeLeft, targetDate, showCountdown, color }) => {
  const remainingTime = Math.max(0, timeLeft);

  if (showCountdown && timeLeft !== null) {
    return (
      <p style={{ color: color || '#1890ff', fontWeight: 'bold', fontSize: '16px' }}>
        {label}: {dayjs.duration(remainingTime, 'seconds').format('HH:mm:ss')}
      </p>
    );
  }

  return (
    <p style={{ color: color || '#1890ff', fontWeight: 'bold', fontSize: '16px' }}>
      {label}: {dayjs.utc(targetDate).local().locale('ru').format('D MMMM YYYY, HH:mm')}
    </p>
  );
};

export default TimeDisplay;


