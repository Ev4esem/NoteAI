import whisper

model = whisper.load_model("turbo", device="cuda")
result = model.transcribe("./static/audio.mp3", language="ru")
print(result["text"])