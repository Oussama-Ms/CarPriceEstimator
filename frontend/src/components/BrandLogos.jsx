import React from 'react';

// Use Simple Icons CDN for reliable, high-quality official SVGs
const CdnLogo = ({ slug, name, className, style }) => (
    <img
        src={`https://cdn.simpleicons.org/${slug}/white`}
        alt={name}
        className={`${className} brand-logo-img`}
        style={{ width: '60px', height: '60px', objectFit: 'contain', transition: 'all 0.3s ease', ...style }}
        onMouseOver={e => {
            e.currentTarget.src = `https://cdn.simpleicons.org/${slug}/d32f2f`; // Red on Hover
            e.currentTarget.style.transform = 'scale(1.2)';
            e.currentTarget.style.filter = 'drop-shadow(0 0 15px rgba(211, 47, 47, 0.8))';
        }}
        onMouseOut={e => {
            e.currentTarget.src = `https://cdn.simpleicons.org/${slug}/white`;
            e.currentTarget.style.transform = 'scale(1)';
            e.currentTarget.style.filter = 'none';
        }}
    />
);

// Local Mercedes Image (User requested "Professional" image, not SVG code)
const MercedesToImage = ({ className, style }) => (
    <img
        src="/assets/logos/mercedes.png"
        alt="Mercedes-Benz"
        className={`${className} brand-logo-img`}
        style={{
            width: '60px',
            height: '60px',
            objectFit: 'contain',
            transition: 'all 0.3s ease',
            filter: 'brightness(0) invert(1)', // Make white initially
            ...style
        }}
        onMouseOver={e => {
            e.currentTarget.style.filter = 'none'; // Show original colors or handle tint? 
            // Actually, for "Red on Hover", we need a different strategy for PNGs if we want to change color.
            // Since it's a PNG, we can't easily change color to specific red #d32f2f without filters.
            // Let's use a filter to make it red-ish.
            e.currentTarget.style.filter = 'brightness(0.5) sepia(1) hue-rotate(-50deg) saturate(5)';
            e.currentTarget.style.transform = 'scale(1.2)';
        }}
        onMouseOut={e => {
            e.currentTarget.style.filter = 'brightness(0) invert(1)'; // Back to white
            e.currentTarget.style.transform = 'scale(1)';
        }}
        onError={(e) => {
            // Fallback if local file fails - try CDN as last resort but hide if broken
            e.target.style.display = 'none';
        }}
    />
);

export const CarLogos = {
    Dacia: (props) => <CdnLogo slug="dacia" name="Dacia" {...props} />,
    Renault: (props) => <CdnLogo slug="renault" name="Renault" {...props} />,
    Peugeot: (props) => <CdnLogo slug="peugeot" name="Peugeot" {...props} />,
    Volkswagen: (props) => <CdnLogo slug="volkswagen" name="Volkswagen" {...props} />,
    Hyundai: (props) => <CdnLogo slug="hyundai" name="Hyundai" {...props} />,
    Toyota: (props) => <CdnLogo slug="toyota" name="Toyota" {...props} />,
    Bmw: (props) => <CdnLogo slug="bmw" name="BMW" {...props} />,
    Mercedes: (props) => <MercedesToImage {...props} />,
};

// Partner Badge for Local Brands
const PartnerBadge = ({ children, className, style }) => (
    <div
        className={className}
        style={{
            background: 'rgba(255, 255, 255, 0.95)',
            borderRadius: '50px',
            padding: '8px 20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 15px rgba(0,0,0,0.5)',
            border: '2px solid rgba(255,255,255,0.1)',
            transition: 'all 0.3s ease',
            height: 'fit-content',
            ...style
        }}
        onMouseOver={e => {
            e.currentTarget.style.transform = 'scale(1.1)';
            e.currentTarget.style.background = '#ffffff';
            e.currentTarget.style.boxShadow = '0 0 20px rgba(255,255,255,0.9)';
        }}
        onMouseOut={e => {
            e.currentTarget.style.transform = 'scale(1)';
            e.currentTarget.style.background = 'rgba(255, 255, 255, 0.95)';
            e.currentTarget.style.boxShadow = '0 0 15px rgba(0,0,0,0.5)';
        }}
    >
        {children}
    </div>
);

export const AvitoLogo = ({ className, style }) => (
    <PartnerBadge className={className} style={style}>
        <img
            src="/assets/logos/avito.jpg"
            alt="Avito.ma"
            style={{
                height: '30px',
                width: 'auto',
                objectFit: 'contain',
                mixBlendMode: 'multiply',
                filter: 'contrast(1.2)'
            }}
            onError={(e) => {
                if (e.target.src.endsWith('jpg')) e.target.src = "/assets/logos/avito.png";
            }}
        />
    </PartnerBadge>
);

export const MoteurLogo = ({ className, style }) => (
    <PartnerBadge className={className} style={style}>
        <img
            src="/assets/logos/moteur.ma.png"
            alt="Moteur.ma"
            style={{ height: '30px', width: 'auto', objectFit: 'contain' }}
            onError={(e) => {
                if (e.target.src.endsWith('png')) e.target.src = "/assets/logos/moteur.png";
            }}
        />
    </PartnerBadge>
);
