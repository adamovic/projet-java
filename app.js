// Application JavaScript pour la bibliothèque universitaire

let currentPage = 'dashboard';
let students = [];
let books = [];
let loans = [];

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    loadDashboard();
    setupNavigation();
    setupSearch();
});

// Navigation
function setupNavigation() {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            showPage(page);
        });
    });
}

function showPage(pageName) {
    // Masquer toutes les pages
    document.querySelectorAll('.page-content').forEach(page => {
        page.style.display = 'none';
    });
    
    // Afficher la page demandée
    document.getElementById(pageName).style.display = 'block';
    
    // Mettre à jour la navigation active
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`[data-page="${pageName}"]`).classList.add('active');
    
    currentPage = pageName;
    
    // Charger les données selon la page
    switch(pageName) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'students':
            loadStudents();
            break;
        case 'books':
            loadBooks();
            break;
        case 'loans':
            loadLoans();
            break;
        case 'search':
            setupSearch();
            break;
    }
}

// API calls
async function apiCall(endpoint, method = 'GET', data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };
        
        if (data && method !== 'GET') {
            options.body = JSON.stringify(data);
        }
        
        const response = await fetch(`/api/${endpoint}`, options);
        return await response.json();
    } catch (error) {
        console.error('Erreur API:', error);
        return null;
    }
}

// Dashboard
async function loadDashboard() {
    const [studentsData, booksData, loansData] = await Promise.all([
        apiCall('students'),
        apiCall('books'),
        apiCall('loans')
    ]);
    
    if (studentsData) {
        students = studentsData;
        document.getElementById('totalStudents').textContent = students.length;
    }
    
    if (booksData) {
        books = booksData;
        document.getElementById('totalBooks').textContent = books.length;
    }
    
    if (loansData) {
        loans = loansData;
        const activeLoans = loans.filter(loan => loan.statut === 'Actif');
        document.getElementById('activeLoans').textContent = activeLoans.length;
        
        const lateLoans = loans.filter(loan => {
            const returnDate = new Date(loan.dateRetourPrevue);
            return returnDate < new Date() && loan.statut === 'Actif';
        });
        document.getElementById('lateLoans').textContent = lateLoans.length;
        
        // Afficher les emprunts récents
        displayRecentLoans(activeLoans.slice(0, 5));
        
        // Afficher les livres populaires
        displayPopularBooks(books.slice(0, 5));
    }
}

function displayRecentLoans(recentLoans) {
    const container = document.getElementById('recentLoans');
    container.innerHTML = '';
    
    recentLoans.forEach(loan => {
        const item = document.createElement('div');
        item.className = 'list-group-item';
        item.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h6 class="mb-1">${loan.livre.titre}</h6>
                    <p class="mb-1 text-muted">${loan.etudiant.prenom} ${loan.etudiant.nom}</p>
                    <small class="text-muted">Emprunté: ${loan.dateEmprunt}</small>
                </div>
                <span class="badge bg-primary rounded-pill">${loan.statut}</span>
            </div>
        `;
        container.appendChild(item);
    });
}

function displayPopularBooks(popularBooks) {
    const container = document.getElementById('popularBooks');
    container.innerHTML = '';
    
    popularBooks.forEach(book => {
        const item = document.createElement('div');
        item.className = 'list-group-item';
        item.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h6 class="mb-1">${book.titre}</h6>
                    <p class="mb-1 text-muted">${book.auteur}</p>
                    <small class="text-muted">${book.categorie}</small>
                </div>
                <span class="badge bg-success rounded-pill">${book.nombreExemplairesDisponibles}/${book.nombreExemplairesTotal}</span>
            </div>
        `;
        container.appendChild(item);
    });
}

// Students
async function loadStudents() {
    const data = await apiCall('students');
    if (data) {
        students = data;
        displayStudents();
    }
}

function displayStudents() {
    const tbody = document.querySelector('#studentsTable tbody');
    tbody.innerHTML = '';
    
    students.forEach(student => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${student.id}</td>
            <td>${student.nom}</td>
            <td>${student.prenom}</td>
            <td>${student.email}</td>
            <td>${student.telephone}</td>
            <td>
                <span class="badge bg-info">${student.nombreEmpruntsActifs}</span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editStudent('${student.id}')">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger btn-action" onclick="deleteStudent('${student.id}')">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function showAddStudentModal() {
    const modal = new bootstrap.Modal(document.getElementById('entityModal'));
    document.getElementById('modalTitle').textContent = 'Ajouter un étudiant';
    document.getElementById('modalBody').innerHTML = `
        <form id="studentForm">
            <div class="mb-3">
                <label class="form-label">ID Étudiant</label>
                <input type="text" class="form-control" id="studentId" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Nom</label>
                <input type="text" class="form-control" id="studentNom" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Prénom</label>
                <input type="text" class="form-control" id="studentPrenom" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" id="studentEmail" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Téléphone</label>
                <input type="tel" class="form-control" id="studentTelephone" required>
            </div>
        </form>
    `;
    
    document.getElementById('saveButton').onclick = saveStudent;
    modal.show();
}

async function saveStudent() {
    console.log('Tentative d\'ajout d\'étudiant...');
    
    const student = {
        id: document.getElementById('studentId').value,
        nom: document.getElementById('studentNom').value,
        prenom: document.getElementById('studentPrenom').value,
        email: document.getElementById('studentEmail').value,
        telephone: document.getElementById('studentTelephone').value,
        nombreEmpruntsActifs: 0
    };
    
    console.log('Données étudiant:', student);
    
    try {
        // Appel API POST pour ajouter l'étudiant
        const result = await apiCall('students', 'POST', student);
        console.log('Résultat API:', result);
        
        if (result && result.success) {
            // Recharger la liste des étudiants
            await loadStudents();
            
            // Fermer le modal
            bootstrap.Modal.getInstance(document.getElementById('entityModal')).hide();
            
            // Afficher un message de succès
            alert('Étudiant ajouté avec succès!');
        } else {
            console.error('Erreur API:', result);
            alert('Erreur lors de l\'ajout de l\'étudiant: ' + (result?.error || 'Erreur inconnue'));
        }
    } catch (error) {
        console.error('Erreur saveStudent:', error);
        alert('Erreur de connexion au serveur');
    }
}

// Books
async function loadBooks() {
    const data = await apiCall('books');
    if (data) {
        books = data;
        displayBooks();
    }
}

function displayBooks() {
    const tbody = document.querySelector('#booksTable tbody');
    tbody.innerHTML = '';
    
    books.forEach(book => {
        const row = document.createElement('tr');
        const availabilityClass = book.nombreExemplairesDisponibles > 0 ? 'success' : 'danger';
        row.innerHTML = `
            <td>${book.isbn}</td>
            <td>${book.titre}</td>
            <td>${book.auteur}</td>
            <td><span class="badge bg-secondary">${book.categorie}</span></td>
            <td>${book.anneePublication}</td>
            <td>
                <span class="badge bg-${availabilityClass}">${book.nombreExemplairesDisponibles}</span>
            </td>
            <td>${book.nombreExemplairesTotal}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editBook('${book.isbn}')">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger btn-action" onclick="deleteBook('${book.isbn}')">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function showAddBookModal() {
    const modal = new bootstrap.Modal(document.getElementById('entityModal'));
    document.getElementById('modalTitle').textContent = 'Ajouter un livre';
    document.getElementById('modalBody').innerHTML = `
        <form id="bookForm">
            <div class="mb-3">
                <label class="form-label">ISBN</label>
                <input type="text" class="form-control" id="bookIsbn" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Titre</label>
                <input type="text" class="form-control" id="bookTitre" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Auteur</label>
                <input type="text" class="form-control" id="bookAuteur" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Catégorie</label>
                <select class="form-select" id="bookCategorie" required>
                    <option value="">Sélectionner...</option>
                    <option value="Roman">Roman</option>
                    <option value="Science-fiction">Science-fiction</option>
                    <option value="Philosophie">Philosophie</option>
                    <option value="Histoire">Histoire</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Année de publication</label>
                <input type="number" class="form-control" id="bookAnnee" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Nombre d'exemplaires</label>
                <input type="number" class="form-control" id="bookExemplaires" required>
            </div>
        </form>
    `;
    
    document.getElementById('saveButton').onclick = saveBook;
    modal.show();
}

function saveBook() {
    const exemplaires = parseInt(document.getElementById('bookExemplaires').value);
    const book = {
        isbn: document.getElementById('bookIsbn').value,
        titre: document.getElementById('bookTitre').value,
        auteur: document.getElementById('bookAuteur').value,
        categorie: document.getElementById('bookCategorie').value,
        anneePublication: parseInt(document.getElementById('bookAnnee').value),
        nombreExemplairesDisponibles: exemplaires,
        nombreExemplairesTotal: exemplaires
    };
    
    // Simuler l'ajout
    books.push(book);
    displayBooks();
    
    // Fermer le modal
    bootstrap.Modal.getInstance(document.getElementById('entityModal')).hide();
}

// Loans
async function loadLoans() {
    const data = await apiCall('loans');
    if (data) {
        loans = data;
        displayLoans();
    }
}

function displayLoans() {
    const tbody = document.querySelector('#loansTable tbody');
    tbody.innerHTML = '';
    
    loans.forEach(loan => {
        const row = document.createElement('tr');
        const statusClass = loan.statut === 'Actif' ? 'success' : 
                           loan.statut === 'Retard' ? 'danger' : 'secondary';
        row.innerHTML = `
            <td>${loan.id}</td>
            <td>${loan.etudiant.prenom} ${loan.etudiant.nom}</td>
            <td>${loan.livre.titre}</td>
            <td>${loan.dateEmprunt}</td>
            <td>${loan.dateRetourPrevue}</td>
            <td>${loan.dateRetour || '-'}</td>
            <td>
                <span class="badge bg-${statusClass}">${loan.statut}</span>
            </td>
            <td>
                ${loan.statut === 'Actif' ? 
                    `<button class="btn btn-sm btn-success btn-action" onclick="returnLoan('${loan.id}')">
                        <i class="fas fa-undo"></i> Retour
                    </button>` : 
                    `<span class="text-muted">Terminé</span>`
                }
            </td>
        `;
        tbody.appendChild(row);
    });
}

function showAddLoanModal() {
    const modal = new bootstrap.Modal(document.getElementById('entityModal'));
    document.getElementById('modalTitle').textContent = 'Nouvel emprunt';
    document.getElementById('modalBody').innerHTML = `
        <form id="loanForm">
            <div class="mb-3">
                <label class="form-label">Étudiant</label>
                <select class="form-select" id="loanStudent" required>
                    <option value="">Sélectionner un étudiant...</option>
                    ${students.map(s => `<option value="${s.id}">${s.prenom} ${s.nom}</option>`).join('')}
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Livre</label>
                <select class="form-select" id="loanBook" required>
                    <option value="">Sélectionner un livre...</option>
                    ${books.filter(b => b.nombreExemplairesDisponibles > 0)
                           .map(b => `<option value="${b.isbn}">${b.titre} (${b.auteur})</option>`).join('')}
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Date de retour prévue</label>
                <input type="date" class="form-control" id="loanReturnDate" required>
            </div>
        </form>
    `;
    
    document.getElementById('saveButton').onclick = saveLoan;
    modal.show();
}

function saveLoan() {
    const studentId = document.getElementById('loanStudent').value;
    const bookIsbn = document.getElementById('loanBook').value;
    
    const student = students.find(s => s.id === studentId);
    const book = books.find(b => b.isbn === bookIsbn);
    
    if (student && book) {
        const loan = {
            id: 'EMP' + (loans.length + 1).toString().padStart(3, '0'),
            etudiant: student,
            livre: book,
            dateEmprunt: new Date().toISOString().split('T')[0],
            dateRetourPrevue: document.getElementById('loanReturnDate').value,
            dateRetour: null,
            statut: 'Actif'
        };
        
        // Mettre à jour les disponibilités
        book.nombreExemplairesDisponibles--;
        student.nombreEmpruntsActifs++;
        
        loans.push(loan);
        displayLoans();
        
        // Fermer le modal
        bootstrap.Modal.getInstance(document.getElementById('entityModal')).hide();
    }
}

function returnLoan(loanId) {
    const loan = loans.find(l => l.id === loanId);
    if (loan) {
        loan.dateRetour = new Date().toISOString().split('T')[0];
        loan.statut = 'Retourné';
        
        // Mettre à jour les disponibilités
        loan.livre.nombreExemplairesDisponibles++;
        loan.etudiant.nombreEmpruntsActifs--;
        
        displayLoans();
    }
}

// Search
function setupSearch() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const availableOnly = document.getElementById('availableOnly');
    
    if (searchInput) {
        searchInput.addEventListener('input', performSearch);
    }
    if (categoryFilter) {
        categoryFilter.addEventListener('change', performSearch);
    }
    if (availableOnly) {
        availableOnly.addEventListener('change', performSearch);
    }
}

async function performSearch() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const category = document.getElementById('categoryFilter').value;
    const availableOnly = document.getElementById('availableOnly').checked;
    
    let filteredBooks = books.filter(book => {
        const matchesSearch = !searchTerm || 
            book.titre.toLowerCase().includes(searchTerm) ||
            book.auteur.toLowerCase().includes(searchTerm) ||
            book.isbn.toLowerCase().includes(searchTerm);
        
        const matchesCategory = !category || book.categorie === category;
        const matchesAvailability = !availableOnly || book.nombreExemplairesDisponibles > 0;
        
        return matchesSearch && matchesCategory && matchesAvailability;
    });
    
    displaySearchResults(filteredBooks);
}

function displaySearchResults(results) {
    const container = document.getElementById('searchResults');
    container.innerHTML = '';
    
    if (results.length === 0) {
        container.innerHTML = '<p class="text-muted">Aucun livre trouvé.</p>';
        return;
    }
    
    results.forEach(book => {
        const card = document.createElement('div');
        card.className = 'col-md-4 mb-3';
        card.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">${book.titre}</h5>
                    <p class="card-text">
                        <strong>Auteur:</strong> ${book.auteur}<br>
                        <strong>ISBN:</strong> ${book.isbn}<br>
                        <strong>Catégorie:</strong> <span class="badge bg-secondary">${book.categorie}</span><br>
                        <strong>Année:</strong> ${book.anneePublication}<br>
                        <strong>Disponibilité:</strong> 
                        <span class="badge bg-${book.nombreExemplairesDisponibles > 0 ? 'success' : 'danger'}">
                            ${book.nombreExemplairesDisponibles}/${book.nombreExemplairesTotal}
                        </span>
                    </p>
                    <button class="btn btn-primary btn-sm" ${book.nombreExemplairesDisponibles === 0 ? 'disabled' : ''}>
                        <i class="fas fa-book-reader"></i> Emprunter
                    </button>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

// Fonctions utilitaires
function editStudent(id) {
    console.log('Modifier étudiant:', id);
}

function deleteStudent(id) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet étudiant?')) {
        students = students.filter(s => s.id !== id);
        displayStudents();
    }
}

function editBook(isbn) {
    console.log('Modifier livre:', isbn);
}

function deleteBook(isbn) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce livre?')) {
        books = books.filter(b => b.isbn !== isbn);
        displayBooks();
    }
}
