import React, { useState, useEffect } from 'react';
import { predictPrice } from '../services/api';
import './PredictionForm.css';
import { CarLogos } from './BrandLogos';

// Mapping strings to Components
const BrandComponents = {
    "DACIA": CarLogos.Dacia,
    "RENAULT": CarLogos.Renault,
    "PEUGEOT": CarLogos.Peugeot,
    "VOLKSWAGEN": CarLogos.Volkswagen,
    "HYUNDAI": CarLogos.Hyundai,
    "TOYOTA": CarLogos.Toyota,
    "BMW": CarLogos.Bmw,
    "MERCEDES-BENZ": CarLogos.Mercedes
};

const PredictionForm = () => {
    const [formData, setFormData] = useState({
        marque: 'DACIA',
        modele: '',
        annee: 2019,
        kilometrage: 100000,
        carburant: 'DIESEL',
        boiteVitesse: 'MANUELLE'
    });

    const [availableModels, setAvailableModels] = useState([]);
    const [prediction, setPrediction] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const carData = {
        "DACIA": ["Logan", "Sandero", "Duster", "Dokker", "Lodgy"],
        "RENAULT": ["Clio", "Megane", "Kangoo"],
        "PEUGEOT": ["206", "208", "Partner"],
        "VOLKSWAGEN": ["T-Roc", "Tiguan", "Touareg"],
        "HYUNDAI": ["i10", "Accent", "Tucson"],
        "TOYOTA": ["Yaris", "Corolla", "RAV4"],
        "MERCEDES-BENZ": ["Classe A", "Classe C", "Classe E"],
        "BMW": ["Série 1", "Série 3", "Série 5"]
    };

    useEffect(() => {
        const models = carData[formData.marque] || [];
        setAvailableModels(models);
        setFormData(prev => ({ ...prev, modele: models[0] || '' }));
    }, [formData.marque]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setPrediction(null);

        try {
            const result = await predictPrice(formData);
            setPrediction(result);
        } catch (err) {
            setError("Failed to get prediction.");
        } finally {
            setLoading(false);
        }
    };

    const SelectedLogo = BrandComponents[formData.marque];

    return (
        <div className="form-container">
            <div className="form-card">
                {/* Dynamic Logo Header */}
                <div className="form-header-logo">
                    {SelectedLogo ? (
                        <SelectedLogo className="selected-brand-icon" />
                    ) : (
                        <h2>{formData.marque}</h2>
                    )}
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label>BRAND</label>
                        <select name="marque" value={formData.marque} onChange={handleChange}>
                            {Object.keys(carData).map(brand => (
                                <option key={brand} value={brand}>{brand}</option>
                            ))}
                        </select>
                    </div>

                    <div className="input-group">
                        <label>MODEL</label>
                        <select name="modele" value={formData.modele} onChange={handleChange}>
                            {availableModels.map(model => (
                                <option key={model} value={model}>{model}</option>
                            ))}
                        </select>
                    </div>

                    <div className="row-group">
                        <div className="input-group">
                            <label>YEAR</label>
                            <input
                                type="number"
                                name="annee"
                                value={formData.annee}
                                onChange={handleChange}
                                min="1990" max="2025"
                            />
                        </div>
                        <div className="input-group">
                            <label>MILEAGE (KM)</label>
                            <input
                                type="number"
                                name="kilometrage"
                                value={formData.kilometrage}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="row-group">
                        <div className="input-group">
                            <label>FUEL</label>
                            <select name="carburant" value={formData.carburant} onChange={handleChange}>
                                <option value="DIESEL">Diesel</option>
                                <option value="ESSENCE">Essence</option>
                                <option value="HYBRIDE">Hybride</option>
                                <option value="ELECTRIQUE">Electric</option>
                            </select>
                        </div>
                        <div className="input-group">
                            <label>TRANSMISSION</label>
                            <select name="boiteVitesse" value={formData.boiteVitesse} onChange={handleChange}>
                                <option value="MANUELLE">Manual</option>
                                <option value="AUTOMATIQUE">Automatic</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" disabled={loading} className="submit-btn">
                        {loading ? 'CALCULATING...' : 'ESTIMATE VALUE'}
                    </button>
                </form>

                {prediction && (
                    <div className="result-box">
                        <span className="result-title">ESTIMATED PRICE</span>
                        <div className="result-value">{prediction}</div>
                    </div>
                )}

                {error && <div className="error-msg">{error}</div>}
            </div>
        </div>
    );
};

export default PredictionForm;
