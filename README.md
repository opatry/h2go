# H₂Go! 💧

**Your minimalist companion for daily water tracking.**

Built with a modern Android stack, **H₂Go!** is designed to be simple, fast, and supportive. Just one tap to log your water, right from the app or your home screen.

## ✨ Key Features

- **1-Tap Logging:** Add water instantly from the app or home screen widget.
- **Live-Updating Widget:** Your progress, always visible on your home screen.
- **Historical Insights:** Visualize your consistency with daily, weekly, and monthly charts.
- **Full Personalization:** Customize your daily goals, glass size, and units (ml/oz).
- **Smart Reminders:** Get gentle nudges when it's time to drink.
- **Data Backup & Restore:** Never lose your progress (via Google Drive).

## 🛠️ Tech Stack & Architecture

This project is a showcase of modern Android development, built with a focus on scalability, testability, and a reactive UI.

### Architecture
- **Clean Architecture:** A clear separation of concerns between each layers. This makes the codebase modular, independent of frameworks, and highly testable.
- **Unidirectional Data Flow (UDF):** The UI observes a single stream of `UiState` from the ViewModel, ensuring a predictable and debuggable state management system.

## 🛠️  Tech stack

- [Kotlin](https://kotlinlang.org/)
- [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
- [Room](https://developer.android.com/training/data-storage/room) for local persistance
- [Koin](https://insert-koin.io/) for dependency injection
- [Material Design 3 Components](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [GitHub Actions](https://docs.github.com/en/actions) for CI
  - build Android & Desktop apps
  - run tests
  - compute code coverage & check threshold
  - publish app on Play Store
  - publish companion website on [Github pages](https://pages.github.com/)

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**. Please check out our [contribution guidelines](CONTRIBUTING.md) and the open [issues](https://github.com/opatry/h2go/your-repo/issues).

## ⚖️  License

```
The MIT License (MIT)

Copyright (c) 2025 Olivier Patry

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```