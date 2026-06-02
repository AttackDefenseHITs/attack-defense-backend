import sound from '../../resources/audio/notification.mp3';
import { notification } from 'antd';

const notificationSound = new Audio(sound);

export const showNotification = (message) => {
  if (!message || !message.message) {
    console.error('Invalid message format:', message);
    return;
  }

  notificationSound.play().catch((error) => {
    console.error('Error playing notification sound:', error);
  });

  notification.info({
    message: 'Внимание!',
    description: message.message,
    placement: 'topRight',
    duration: 5,
  });
};

