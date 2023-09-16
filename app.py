import pandas as pd
from flask import Flask, request, jsonify

# Load your dataset into a Pandas DataFrame
data = pd.read_csv('crop_recommendation_2.csv')

X = data[['temperature', 'humidity', 'ph', 'rainfall']]
y = data['label']

# Train a Random Forest classifier
from sklearn.ensemble import RandomForestClassifier

model = RandomForestClassifier()
model.fit(X, y)

# Create a Flask app
app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Get input data from the JSON request
        request_data = request.json

        temperature = request_data.get('temperature')
        humidity = request_data.get('humidity')
        ph = request_data.get('ph')
        rainfall = request_data.get('rainfall')

        if None in (temperature, humidity, ph, rainfall):
            return jsonify({'error': 'Please provide temperature, humidity, ph, and rainfall.'}), 400

        # Create a DataFrame with the input data
        input_data = pd.DataFrame([[temperature, humidity, ph, rainfall]],
                                   columns=['temperature', 'humidity', 'ph', 'rainfall'])

        # Get the predicted probabilities for the input data
        probabilities = model.predict_proba(input_data)

        # Retrieve the probabilities for the positive class
        crop_probabilities = dict(zip(model.classes_, probabilities[0]))

        # Sort the crop probabilities in descending order
        sorted_crops = sorted(crop_probabilities.items(), key=lambda x: x[1], reverse=True)

        # Retrieve the top 10 crops
        top_10_crops = sorted_crops[:10]

        # Prepare the result as a list of dictionaries
        result = [{'crop': crop, 'probability': probability} for crop, probability in top_10_crops]

        return jsonify(result)

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
