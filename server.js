const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

// Base de données en mémoire
let students = [
  { id: 'ETU001', nom: 'Dupont', prenom: 'Jean', email: 'jean.dupont@univ.fr', telephone: '0123456789', nombreEmpruntsActifs: 2 },
  { id: 'ETU002', nom: 'Martin', prenom: 'Marie', email: 'marie.martin@univ.fr', telephone: '0987654321', nombreEmpruntsActifs: 1 },
  { id: 'ETU003', nom: 'Bernard', prenom: 'Pierre', email: 'pierre.bernard@univ.fr', telephone: '0612345678', nombreEmpruntsActifs: 0 }
];

let books = [
  { isbn: '978-2-07-041464-4', titre: 'Le Petit Prince', auteur: 'Antoine de Saint-Exupéry', categorie: 'Roman', anneePublication: 1943, nombreExemplairesDisponibles: 3, nombreExemplairesTotal: 5 },
  { isbn: '978-2-253-05656-5', titre: '1984', auteur: 'George Orwell', categorie: 'Science-fiction', anneePublication: 1949, nombreExemplairesDisponibles: 2, nombreExemplairesTotal: 3 },
  { isbn: '978-2-07-036002-4', titre: 'L\'Étranger', auteur: 'Albert Camus', categorie: 'Roman', anneePublication: 1942, nombreExemplairesDisponibles: 4, nombreExemplairesTotal: 4 }
];

let loans = [
  { id: 'EMP001', etudiant: { id: 'ETU001', nom: 'Dupont', prenom: 'Jean' }, livre: { isbn: '978-2-07-041464-4', titre: 'Le Petit Prince' }, dateEmprunt: '2024-01-15', dateRetourPrevue: '2024-02-15', dateRetour: null, statut: 'Actif' },
  { id: 'EMP002', etudiant: { id: 'ETU002', nom: 'Martin', prenom: 'Marie' }, livre: { isbn: '978-2-253-05656-5', titre: '1984' }, dateEmprunt: '2024-01-20', dateRetourPrevue: '2024-02-20', dateRetour: null, statut: 'Actif' }
];

// Types de fichiers MIME
const mimeTypes = {
  '.html': 'text/html',
  '.js': 'text/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.wav': 'audio/wav',
  '.mp4': 'video/mp4',
  '.woff': 'application/font-woff',
  '.ttf': 'application/font-ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.otf': 'application/font-otf',
  '.wasm': 'application/wasm'
};

const server = http.createServer((req, res) => {
  console.log(`${req.method} ${req.url}`);

  // Gestion des routes API
  if (req.url.startsWith('/api/')) {
    handleApiRequest(req, res);
    return;
  }

  // Servir les fichiers statiques
  let filePath = '.' + req.url;
  if (filePath === './') {
    filePath = './index.html';
  }

  const extname = String(path.extname(filePath)).toLowerCase();
  const mimeType = mimeTypes[extname] || 'application/octet-stream';

  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === 'ENOENT') {
        // Fichier non trouvé, essayer index.html
        fs.readFile('./index.html', (error, content) => {
          if (error) {
            res.writeHead(404, { 'Content-Type': 'text/html' });
            res.end('<h1>404 Not Found</h1>', 'utf-8');
          } else {
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(content, 'utf-8');
          }
        });
      } else {
        // Erreur serveur
        res.writeHead(500, { 'Content-Type': 'text/html' });
        res.end('<h1>500 Internal Server Error</h1>', 'utf-8');
      }
    } else {
      res.writeHead(200, { 'Content-Type': mimeType });
      res.end(content, 'utf-8');
    }
  });
});

// Gestion des requêtes API pour interagir avec l'application Java
function handleApiRequest(req, res) {
  res.setHeader('Content-Type', 'application/json');
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  const urlParts = req.url.split('/');
  const resource = urlParts[2];

  // Simuler des réponses API pour la bibliothèque
  switch (resource) {
    case 'students':
      handleStudents(req, res);
      break;
    case 'books':
      handleBooks(req, res);
      break;
    case 'loans':
      handleLoans(req, res);
      break;
    case 'database':
      handleDatabase(req, res);
      break;
    default:
      res.writeHead(404);
      res.end(JSON.stringify({ error: 'Resource not found' }));
  }
}

// Handlers API
function handleStudents(req, res) {
  let body = '';
  
  req.on('data', chunk => {
    body += chunk.toString();
  });
  
  req.on('end', () => {
    if (req.method === 'POST') {
      // Ajouter un nouvel étudiant
      try {
        const newStudent = JSON.parse(body);
        students.push(newStudent);
        console.log('Nouvel étudiant ajouté:', newStudent);
        console.log('Total étudiants:', students.length);
        res.writeHead(201);
        res.end(JSON.stringify({ success: true, student: newStudent }));
      } catch (error) {
        console.error('Erreur parsing JSON:', error);
        res.writeHead(400);
        res.end(JSON.stringify({ error: 'Données invalides' }));
      }
    } else {
      // GET - retourner la liste des étudiants
      console.log('Envoi de', students.length, 'étudiants');
      res.writeHead(200);
      res.end(JSON.stringify(students));
    }
  });
}

function handleBooks(req, res) {
  if (req.method === 'POST') {
    let body = '';
    req.on('data', chunk => {
      body += chunk.toString();
    });
    req.on('end', () => {
      try {
        const newBook = JSON.parse(body);
        books.push(newBook);
        console.log('Nouveau livre ajouté:', newBook);
        res.writeHead(201);
        res.end(JSON.stringify({ success: true, book: newBook }));
      } catch (error) {
        res.writeHead(400);
        res.end(JSON.stringify({ error: 'Données invalides' }));
      }
    });
  } else {
    res.writeHead(200);
    res.end(JSON.stringify(books));
  }
}

function handleLoans(req, res) {
  if (req.method === 'POST') {
    let body = '';
    req.on('data', chunk => {
      body += chunk.toString();
    });
    req.on('end', () => {
      try {
        const newLoan = JSON.parse(body);
        loans.push(newLoan);
        console.log('Nouvel emprunt ajouté:', newLoan);
        res.writeHead(201);
        res.end(JSON.stringify({ success: true, loan: newLoan }));
      } catch (error) {
        res.writeHead(400);
        res.end(JSON.stringify({ error: 'Données invalides' }));
      }
    });
  } else {
    res.writeHead(200);
    res.end(JSON.stringify(loans));
  }
}

function handleDatabase(req, res) {
  res.writeHead(200);
  res.end(JSON.stringify({
    students: students,
    books: books,
    loans: loans,
    statistics: {
      totalStudents: students.length,
      totalBooks: books.length,
      totalLoans: loans.length,
      activeLoans: loans.filter(loan => loan.statut === 'Actif').length,
      availableBooks: books.reduce((sum, book) => sum + book.nombreExemplairesDisponibles, 0)
    }
  }));
}

server.listen(PORT, () => {
  console.log(`Serveur démarré sur http://localhost:${PORT}`);
  console.log('Appuyez sur Ctrl+C pour arrêter le serveur');
});
