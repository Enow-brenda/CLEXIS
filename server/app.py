from fastapi import FastAPI
from server.routes.resources import router as resource_router

app = FastAPI()


app.include_router(resource_router, prefix="/api/v1/clexis_ai")
# tags are used to group routes under the same group
@app.get("/", tags=["Root"])
async def read_root():
    return {"message": "Welcome clexis AI service app!"}