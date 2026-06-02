import { createContext, useContext, useEffect, useRef, useState } from 'react';
import { API_URLS } from '../constants/apiUrls';
import { showNotification } from '../components/common/SoundNotification';
 
export const EventWebSocketContext = createContext(null);

export const EventWebSocketProvider = ({ children }) => {
  const [messages, setMessages] = useState([]);
  const socketRef = useRef(null);
  const eventHandlersRef = useRef(new Map());

  const connectWebSocket = () => {
    const token = localStorage.getItem('accessToken');
    const socket = new WebSocket(`${API_URLS.BASE_WS}/event?token=${token}`);
    socketRef.current = socket;

    socket.onopen = () => {
      console.log('WebSocket connected');
    };

    socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      console.log('WebSocket event received:', message);
      setMessages((prev) => [...prev, message]);
      dispatchEventHandlers(message);
      showNotification(message);
    };

    socket.onclose = () => {
      console.log('WebSocket disconnected. Attempting to reconnect...');
      setTimeout(() => connectWebSocket(), 5000);
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  };

  const sendMessage = (data) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(data));
    } else {
      console.error('WebSocket is not connected.');
    }
  };

  const registerEventHandler = (eventType, handler) => {
    if (!eventHandlersRef.current.has(eventType)) {
      eventHandlersRef.current.set(eventType, []);
    }
    eventHandlersRef.current.get(eventType).push(handler);
  };

  const unregisterEventHandler = (eventType, handler) => {
    const handlers = eventHandlersRef.current.get(eventType);
    if (handlers) {
      eventHandlersRef.current.set(eventType, handlers.filter((h) => h !== handler));
    }
  };

  const dispatchEventHandlers = (message) => {
    const handlers = eventHandlersRef.current.get(message.eventType);
    if (handlers) {
      handlers.forEach((handler) => handler(message));
    }
  };

  useEffect(() => {
    connectWebSocket();
    return () => {
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, []);

  return (
    <EventWebSocketContext.Provider
      value={{
        messages,
        sendMessage,
        registerEventHandler,
        unregisterEventHandler,
      }}
    >
      {children}
    </EventWebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(EventWebSocketContext);
