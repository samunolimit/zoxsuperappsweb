# Deploy ZOX Super Apps

## Render

1. Push this project to a GitHub repository.
2. Open https://render.com and choose **New > Blueprint**.
3. Select the GitHub repository containing this project.
4. Render reads `render.yaml` and creates the web service.
5. Open the generated `https://...onrender.com` URL.
6. Verify `https://...onrender.com/api/health` returns JSON with `ok: true`.

The service uses:

- Build command: `npm install`
- Start command: `node server/server.js`
- Health check: `/api/health`

The current demo booking store writes to `data/zox.json`. The `data/` folder is ignored by Git. For permanent production data across deploys, connect a managed database such as Supabase/PostgreSQL before launch.
