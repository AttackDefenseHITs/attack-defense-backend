import React, { createContext, useContext, useEffect, useRef, useState } from 'react';
import { API_URLS } from '../constants/apiUrls';

export const CheckerWebSocketContext = createContext(null);

export const CheckerWebSocketProvider = ({ children, active }) => {
  const [messages, setMessages] = useState([]);
  const socketRef = useRef(null);
  const eventHandlersRef = useRef(new Map());

  const connectWebSocket = () => {
    const token = localStorage.getItem('accessToken');
    const socket = new WebSocket(`${API_URLS.BASE_WS}/checker?token=${token}`);
    socketRef.current = socket;

    socket.onopen = () => {};

    socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      console.log('CHECKER WebSocket event received:', message);
      setMessages((prev) => [...prev, message]);
      dispatchEventHandlers(message);
    };

    socket.onclose = () => {};

    socket.onerror = (error) => {
      console.error('Checker WebSocket error:', error);
    };
  };

  const disconnectWebSocket = () => {
    if (socketRef.current) {
      socketRef.current.close();
      socketRef.current = null;
    }
  };

  const sendMessage = (data) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(data));
    } else {
      console.error('Checker WebSocket is not connected.');
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
    if (active) {
      connectWebSocket();
    } else {
      disconnectWebSocket();
    }

    return () => {
      disconnectWebSocket();
    };
  }, [active]);

  return (
    <CheckerWebSocketContext.Provider
      value={{
        messages,
        sendMessage,
        registerEventHandler,
        unregisterEventHandler,
      }}
    >
      {children}
    </CheckerWebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(CheckerWebSocketContext);
