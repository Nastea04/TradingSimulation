import { useEffect, useRef, useState } from "react";

export default function useKrakenPrices(symbols, reconnectDelay = 3000) {
    const [prices, setPrices] = useState({});
    const wsRef = useRef(null);
    const heartbeatRef = useRef(null);
    const reconnectRef = useRef(null);
    const symbolsRef = useRef([]);
    const hasConnectedRef = useRef(false);

    const connect = () => {
        if (!symbolsRef.current?.length) {
            console.warn("No symbols yet");
            return;
        }

        console.log("Connecting...");
        const ws = new WebSocket("wss://ws.kraken.com/v2");
        wsRef.current = ws;

        ws.onopen = () => {
            console.log("Connected");

            ws.send(JSON.stringify({
                method: "subscribe",
                params: {
                    channel: "ticker",
                    symbol: symbolsRef.current
                }
            }));

            startHeartbeat(ws);
        };

        ws.onmessage = (event) => {
            let msg;
            try {
                msg = JSON.parse(event.data);
            } catch {
                return;
            }

            if (msg.channel === "ticker" && msg.data?.length) {
                const symbol = msg.data[0].symbol;
                const last = msg.data[0].last;
                if (last) {
                    setPrices(prev => ({
                        ...prev,
                        [symbol]: parseFloat(last)
                    }));
                }
            }
        };

        ws.onerror = (err) => {
            console.error("WebSocket error:", err);
        };

        ws.onclose = () => {
            console.warn("Disconnected");
            stopHeartbeat();
            scheduleReconnect();
        };
    };

    const startHeartbeat = (ws) => {
        stopHeartbeat();
        heartbeatRef.current = setInterval(() => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ method: "ping" }));
                console.log("ping");
            }
        }, 15000);
    };

    const stopHeartbeat = () => {
        if (heartbeatRef.current) {
            clearInterval(heartbeatRef.current);
            heartbeatRef.current = null;
        }
    };

    const scheduleReconnect = () => {
        if (reconnectRef.current) return;
        reconnectRef.current = setTimeout(() => {
            console.log("Reconnecting...");
            reconnectRef.current = null;
            connect();
        }, reconnectDelay);
    };

    useEffect(() => {
        if (!symbols.length) return;
        symbolsRef.current = symbols;

        if (!hasConnectedRef.current) {
            hasConnectedRef.current = true;
            connect();
        } else if (wsRef.current?.readyState === WebSocket.OPEN) {
            wsRef.current.send(JSON.stringify({
                method: "subscribe",
                params: {
                    channel: "ticker",
                    symbol: symbols
                }
            }));
        }
    }, [symbols]);

    useEffect(() => {
        return () => {
            stopHeartbeat();
            if (wsRef.current) wsRef.current.close();
            if (reconnectRef.current) clearTimeout(reconnectRef.current);
        };
    }, []);

    return prices;
}
