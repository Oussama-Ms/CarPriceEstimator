import React from 'react';

// Replicating the "Original" Professional Look with Inline SVGs (No Crashes)
const TechIconSvg = ({ path, title, color }) => (
    <svg
        viewBox="0 0 24 24"
        width="40"
        height="40"
        className="tech-icon"
        style={{ transition: 'all 0.3s ease', fill: '#888' }} // Default Gray
        onMouseOver={e => {
            e.currentTarget.style.fill = color; // Original Brand Color
            e.currentTarget.style.transform = 'scale(1.2) rotate(-5deg)';
            e.currentTarget.style.filter = `drop-shadow(0 0 8px ${color}80)`; // Add glow
        }}
        onMouseOut={e => {
            e.currentTarget.style.fill = '#888';
            e.currentTarget.style.transform = 'scale(1) rotate(0deg)';
            e.currentTarget.style.filter = 'none';
        }}
    >
        <title>{title}</title>
        <path d={path} />
    </svg>
);

export const TechIcons = {
    // Java (Spring Boot color #6DB33F)
    Spring: () => <TechIconSvg title="Spring Boot" color="#6DB33F" path="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 2.2a9.78 9.78 0 0 1 9.8 9.8A9.78 9.78 0 0 1 12 21.8 9.78 9.78 0 0 1 2.2 12 9.78 9.78 0 0 1 12 2.2zm4.18 5.48c-1.35-.38-2.61.16-3.23.95l-.12.18-.54 1.15c-.14.3-.22.61-.19.92 1.48-1.55 3.52-1.63 4.29-1.42.34.09.61.35.59.88-.04.91-1.05 1.77-2.6 1.77-.38 0-.74-.05-1.07-.15-.49-.15-.89-.43-1.18-.79-.53.6-1.39.73-2.12.39-.77-.35-1.07-1.17-.67-1.92s1.42-.92 2.19-.57c.3.14.54.4.67.72l.48-1c.36-.67.92-1.16 1.62-1.36.19-.05.38-.08.57-.08.47 0 .91.13 1.31.33z" />, // Simplified Leaf

    // MySQL (#4479A1)
    Mysql: () => <TechIconSvg title="MySQL" color="#4479A1" path="M12 0C5.37 0 0 5.37 0 12s5.37 12 12 12 12-5.37 12-12S18.63 0 12 0zm6.8 17.5c-.8.8-1.8 1.4-2.9 1.7-.3.1-.6.2-.9.2-1.9.4-3.7-.1-5.3-1.1-.3-.2-.5-.4-.7-.6-.8-.8-1.4-1.8-1.8-2.9-.1-.3-.2-.6-.2-.9.1-1.9.8-3.6 2.1-4.9l.2-.2c1-.9 2.2-1.5 3.5-1.7 2.1-.3 4 .3 5.6 1.8l.2.2c.4.4.7.9 1 1.4.1.2.2.4.3.5.6 1.4.7 2.9.3 4.4-.1.4-.3.9-.5 1.3-.1.3-.2.5-.3.8-.1.0-.1.0 0 0z" />, // Simplified Dolphin/Logo

    // GitHub (#181717 - but using White for Dark Mode hover)
    Github: () => <TechIconSvg title="GitHub" color="#ffffff" path="M12 .3a12 12 0 0 0-3.8 23.38c.6.12.83-.26.83-.57L9 21.07c-3.34.72-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.1-.74.08-.73.08-.73 1.2.09 1.83 1.24 1.83 1.24 1.08 1.86 2.8 1.32 3.5.99.1-.8.42-1.33.76-1.65-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.46-2.38 1.24-3.22-.13-.3-.54-1.52.12-3.18 0 0 1-.32 3.3 1.23a11.5 11.5 0 0 1 6 0c2.28-1.55 3.29-1.23 3.29-1.23.66 1.66.25 2.88.13 3.18.77.84 1.23 1.91 1.23 3.22 0 4.61-2.8 5.63-5.48 5.92.42.36.81 1.1.81 2.22l-.01 3.29c0 .31.22.69.82.57A12 12 0 0 0 12 .3z" />
};
