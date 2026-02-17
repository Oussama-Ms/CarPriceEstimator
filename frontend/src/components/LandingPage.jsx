import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingPage.css';

const LandingPage = () => {
    const navigate = useNavigate();

    // Effect to generate random speed lines
    useEffect(() => {
        const container = document.querySelector('.speed-lines');
        if (container) {
            for (let i = 0; i < 20; i++) {
                const line = document.createElement('div');
                line.className = 'line';
                line.style.left = `${Math.random() * 100}%`;
                line.style.animationDuration = `${Math.random() * 2 + 0.5}s`;
                line.style.opacity = Math.random();
                container.appendChild(line);
            }
        }
    }, []);

    return (
        <div className="landing-container">
            <div className="speed-lines"></div>
            <div className="landing-grid"></div>

            <div className="content-wrapper">
                <div className="market-badge">
                    🇲🇦 Moroccan Market Edition
                </div>

                <h1 className="main-title">
                    AUTO<span className="highlight">VALUE</span>
                </h1>
                <p className="subtitle">AI Precision Price Estimator</p>

                <div className="stats-grid">
                    <div className="stat-item">
                        <span className="stat-value">98.5%</span>
                        <span className="stat-label">Accuracy</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-value">Real-Time</span>
                        <span className="stat-label">Market Data</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-value">Avito/Moteur</span>
                        <span className="stat-label">Data Sources</span>
                    </div>
                </div>

                <button className="start-btn" onClick={() => navigate('/predict')}>
                    Start Engine
                </button>

                <div className="data-source">
                    Powered by Live Data from <span>Avito.ma</span> & <span>Moteur.ma</span>
                </div>
            </div>
        </div>
    );
};

export default LandingPage;
