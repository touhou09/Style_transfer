from fastapi import FastAPI
from routers import items
# from .dependencies import some_common_dependency

app = FastAPI()

app.include_router(items.router, prefix="/items")
