"""
GPT4All Model Manager
Handles loading and inference with GPT4All models.
"""

from gpt4all import GPT4All
import os
from typing import Optional
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class ModelManager:
    """Manages GPT4All model loading and inference."""
    
    _instance: Optional['ModelManager'] = None
    _model: Optional[GPT4All] = None
    
    MODEL_NAME = os.getenv("GPT4ALL_MODEL", "orca-mini-3b-gguf2-q4_0.gguf")
    MODELS_DIR = os.getenv("GPT4ALL_MODELS_DIR", "/app/models")
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance
    
    def load_model(self) -> GPT4All:
        """Load the GPT4All model (lazy loading)."""
        if self._model is None:
            logger.info(f"Loading GPT4All model: {self.MODEL_NAME}")
            logger.info(f"Models directory: {self.MODELS_DIR}")
            
            # Create models directory if it doesn't exist
            os.makedirs(self.MODELS_DIR, exist_ok=True)
            
            self._model = GPT4All(
                model_name=self.MODEL_NAME,
                model_path=self.MODELS_DIR,
                allow_download=True
            )
            logger.info("Model loaded successfully!")
        return self._model
    
    def generate_response(
        self, 
        prompt: str, 
        system_prompt: str = "",
        max_tokens: int = 500,
        temperature: float = 0.7
    ) -> str:
        """
        Generate a response from the model.
        
        Args:
            prompt: User's message
            system_prompt: System context (optional)
            max_tokens: Maximum tokens to generate
            temperature: Creativity of responses (0.0-1.0)
            
        Returns:
            Generated response text
        """
        model = self.load_model()
        
        full_prompt = prompt
        if system_prompt:
            full_prompt = f"System: {system_prompt}\n\nUser: {prompt}\n\nAssistant:"
        
        with model.chat_session():
            response = model.generate(
                full_prompt,
                max_tokens=max_tokens,
                temp=temperature
            )
        
        return response.strip()
    
    def health_check(self) -> dict:
        """Check if model is loaded and ready."""
        return {
            "status": "healthy" if self._model else "not_loaded",
            "model_name": self.MODEL_NAME,
            "models_dir": self.MODELS_DIR
        }


# Singleton instance
model_manager = ModelManager()
