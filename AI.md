# AI Usage Log

## A-BetterGui
- **Issue**: Make profile photos circular and smaller.
- **Tool**: ChatGPT (GPT-5).
- **Key Tips**:
    - Use `ImageView` + `Circle` clip; optional snapshot to `Image`.
    - Control display size via `ImageView.setFitWidth/Height` or CSS (`-fx-fit-width/-fx-fit-height`).
- **Changes Made**: Added `makeCircularSnapshot(...)` in `MainWindow`; sized avatars via CSS.
- **Time Saved**: ~1 hour.

