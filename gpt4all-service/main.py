"""
GPT4All FastAPI Service
REST API for interacting with GPT4All models.
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional
import logging
import time

from model_manager import model_manager

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# FastAPI app
app = FastAPI(
    title="GPT4All Chat API",
    description="REST API for GPT4All language model",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Request/Response models
class ChatRequest(BaseModel):
    """Chat request model."""
    message: str = Field(..., description="User's message", min_length=1)
    system_prompt: Optional[str] = Field(
        default="Você é um assistente financeiro amigável e prestativo. "
                "Responda em português do Brasil. "
                "Ajude o usuário com dúvidas sobre finanças pessoais, "
                "orçamento, investimentos e economia.",
        description="System prompt for context"
    )
    max_tokens: Optional[int] = Field(default=500, ge=50, le=2000)
    temperature: Optional[float] = Field(default=0.7, ge=0.0, le=1.0)


class ChatResponse(BaseModel):
    """Chat response model."""
    response: str
    processing_time_ms: float
    model_name: str


class HealthResponse(BaseModel):
    """Health check response model."""
    status: str
    model_name: str
    models_dir: str


@app.on_event("startup")
async def startup_event():
    """Pre-load the model on startup."""
    logger.info("Starting GPT4All API server...")
    try:
        model_manager.load_model()
        logger.info("Model pre-loaded successfully!")
    except Exception as e:
        logger.warning(f"Could not pre-load model: {e}")
        logger.info("Model will be loaded on first request.")


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Check if the service is healthy and model is loaded."""
    return model_manager.health_check()


@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """
    Send a message to GPT4All and get a response.
    
    - **message**: The user's message/question
    - **system_prompt**: Optional system context
    - **max_tokens**: Maximum response length (50-2000)
    - **temperature**: Creativity (0.0-1.0)
    """
    start_time = time.time()
    
    try:
        response = model_manager.generate_response(
            prompt=request.message,
            system_prompt=request.system_prompt or "",
            max_tokens=request.max_tokens or 500,
            temperature=request.temperature or 0.7
        )
        
        processing_time = (time.time() - start_time) * 1000
        
        return ChatResponse(
            response=response,
            processing_time_ms=round(processing_time, 2),
            model_name=model_manager.MODEL_NAME
        )
    
    except Exception as e:
        logger.error(f"Error generating response: {e}")
        raise HTTPException(
            status_code=500,
            detail=f"Error generating response: {str(e)}"
        )


@app.get("/")
async def root():
    """Root endpoint with API information."""
    return {
        "service": "GPT4All Chat API",
        "version": "1.0.0",
        "endpoints": {
            "chat": "POST /chat",
            "health": "GET /health"
        }
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)
