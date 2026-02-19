import React from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingPage.css';
import { CarLogos, AvitoLogo, MoteurLogo } from './BrandLogos';

// Using Simple Icons CDN for "Real" Official Tech Stack Logos
const TechIcon = ({ slug, title, color, hoverColor }) => (
    <img
        src={`https://cdn.simpleicons.org/${slug}/${color}`}
        alt={title}
        title={title}
        className="tech-icon"
        style={{ width: '40px', height: '40px', transition: 'all 0.3s ease' }}
        onMouseOver={e => {
            e.currentTarget.src = `https://cdn.simpleicons.org/${slug}/${hoverColor}`;
            e.currentTarget.style.transform = 'scale(1.2)';
            e.currentTarget.style.filter = `drop-shadow(0 0 10px #${hoverColor})`;
        }}
        onMouseOut={e => {
            e.currentTarget.src = `https://cdn.simpleicons.org/${slug}/${color}`;
            e.currentTarget.style.transform = 'scale(1)';
            e.currentTarget.style.filter = 'none';
        }}
    />
);

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <div className="landing-container">
            {/* Top Brand Strip - Using Real CDN Logos */}
            <div className="brand-strip">
                <CarLogos.Dacia className="brand-logo-icon" />
                <CarLogos.Renault className="brand-logo-icon" />
                <CarLogos.Peugeot className="brand-logo-icon" />
                <CarLogos.Volkswagen className="brand-logo-icon" />
                <CarLogos.Hyundai className="brand-logo-icon" />
                <CarLogos.Toyota className="brand-logo-icon" />
                <CarLogos.Bmw className="brand-logo-icon" />
                <CarLogos.Mercedes className="brand-logo-icon" />
            </div>

            {/* Hero Section */}
            <div className="hero-section">
                <h1 className="title-main">AUTOVALUE</h1>
                <h2 className="subtitle-main">INTELLIGENT CAR PRICE ESTIMATOR</h2>

                <p className="description-text">
                    Unlock the true market value of any vehicle.
                    Engineered with state-of-the-art Machine Learning specifically for the Moroccan automotive market.
                </p>

                <button className="cta-button" onClick={() => navigate('/predict')}>
                    Start Estimation
                </button>
            </div>

            {/* Footer / Credits */}
            <div className="footer-section">

                {/* Left Side: Credits */}
                <div className="credits-block">
                    <h4>DEVELOPED BY</h4>
                    <div className="credits-names">
                        <a href="https://www.linkedin.com/in/oussama-ms" target="_blank" rel="noopener noreferrer" className="credits-link">
                            M'SAAD OUSSAMA
                        </a>
                        <a href="https://www.linkedin.com/in/" target="_blank" rel="noopener noreferrer" className="credits-link">
                            SADIQUI YOUSSEF
                        </a>
                    </div>
                </div>

                {/* Right Side: Tech Stack & Sources */}
                <div className="right-footer">
                    <div className="tech-stack">
                        {/* React JS: Official Cyan #61DAFB */}
                        <a href="https://reactjs.org" target="_blank" className="tech-logo-link">
                            <TechIcon slug="react" title="React JS" color="gray" hoverColor="61DAFB" />
                        </a>
                        {/* Spring Boot: Official Green #6DB33F */}
                        <a href="https://spring.io/projects/spring-boot" target="_blank" className="tech-logo-link">
                            <TechIcon slug="springboot" title="Spring Boot" color="gray" hoverColor="6DB33F" />
                        </a>
                        {/* MySQL: Official Blue #4479A1 */}
                        <a href="https://www.mysql.com" target="_blank" className="tech-logo-link">
                            <TechIcon slug="mysql" title="MySQL" color="gray" hoverColor="4479A1" />
                        </a>
                        {/* GitHub: White/Black */}
                        <a href="https://github.com" target="_blank" className="tech-logo-link">
                            <TechIcon slug="github" title="GitHub" color="gray" hoverColor="ffffff" />
                        </a>
                    </div>

                    <div className="data-sources">
                        <span className="data-source-label">Powered By</span>
                        <div className="source-link-wrapper">
                            <AvitoLogo className="data-source-logo" />
                        </div>
                        <div className="source-link-wrapper">
                            <MoteurLogo className="data-source-logo" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LandingPage;
