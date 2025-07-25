// Función para copiar código al portapapeles
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        // Mostrar feedback visual
        showCopyFeedback();
    }).catch(function(err) {
        console.error('Error al copiar: ', err);
    });
}

// Mostrar feedback visual cuando se copia código
function showCopyFeedback() {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.textContent = '✅ ¡Código copiado al portapapeles!';
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #6ab04c;
        color: white;
        padding: 15px 25px;
        border-radius: 8px;
        font-weight: 600;
        z-index: 1000;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        transform: translateX(100%);
        transition: transform 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // Animar entrada
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Animar salida y eliminar
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Animación de scroll suave para enlaces internos
document.addEventListener('DOMContentLoaded', function() {
    // Animación de elementos al hacer scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);
    
    // Observar elementos animables
    const animatedElements = document.querySelectorAll('.feature-card, .tech-item, .step, .install-card, .arch-component');
    animatedElements.forEach(element => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(30px)';
        element.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(element);
    });
    
    // Efecto parallax sutil en el hero
    window.addEventListener('scroll', function() {
        const scrolled = window.pageYOffset;
        const parallax = document.querySelector('.hero');
        const speed = scrolled * 0.5;
        
        if (parallax) {
            parallax.style.transform = `translateY(${speed}px)`;
        }
    });
    
    // Mejorar botones de la aplicación mockup
    const appButtons = document.querySelectorAll('.app-btn');
    appButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Efecto visual de click
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 150);
            
            // Mostrar mensaje según el botón
            const buttonText = this.textContent;
            showActionFeedback(buttonText);
        });
    });
});

// Mostrar feedback para acciones de la aplicación mockup
function showActionFeedback(action) {
    const messages = {
        'Cifrar Archivo': '🔒 Seleccionando archivo para cifrar...',
        'Descifrar Archivo': '🔓 Seleccionando archivo para descifrar...',
        'Crear Usuario': '👤 Abriendo formulario de nuevo usuario...',
        'Ver Claves': '🔑 Mostrando claves almacenadas...'
    };
    
    const message = messages[action] || '⚡ Acción ejecutada';
    
    const notification = document.createElement('div');
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: rgba(47, 54, 64, 0.95);
        color: white;
        padding: 20px 30px;
        border-radius: 12px;
        font-weight: 600;
        z-index: 1000;
        box-shadow: 0 8px 25px rgba(0,0,0,0.3);
        backdrop-filter: blur(10px);
        opacity: 0;
        transition: opacity 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // Animar entrada
    setTimeout(() => {
        notification.style.opacity = '1';
    }, 100);
    
    // Animar salida y eliminar
    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 2000);
}

// Efecto de typing en el título (opcional)
function typeWriter(element, text, speed = 100) {
    let i = 0;
    element.innerHTML = '';
    
    function type() {
        if (i < text.length) {
            element.innerHTML += text.charAt(i);
            i++;
            setTimeout(type, speed);
        }
    }
    
    type();
}

// Smooth scrolling para navegación (si se añaden enlaces de navegación)
function smoothScroll(target) {
    const element = document.querySelector(target);
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// Detectar sistema operativo para mostrar comandos apropiados
function detectOS() {
    const userAgent = window.navigator.userAgent;
    const platform = window.navigator.platform;
    const macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'];
    const windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'];
    const iosPlatforms = ['iPhone', 'iPad', 'iPod'];
    
    let os = 'Unknown';
    
    if (macosPlatforms.indexOf(platform) !== -1) {
        os = 'Mac';
    } else if (iosPlatforms.indexOf(platform) !== -1) {
        os = 'iOS';
    } else if (windowsPlatforms.indexOf(platform) !== -1) {
        os = 'Windows';
    } else if (/Android/.test(userAgent)) {
        os = 'Android';
    } else if (/Linux/.test(platform)) {
        os = 'Linux';
    }
    
    return os;
}

// Actualizar comandos según el OS
document.addEventListener('DOMContentLoaded', function() {
    const os = detectOS();
    const codeBlocks = document.querySelectorAll('.code-block code');
    
    codeBlocks.forEach(code => {
        if (code.textContent.includes('&&')) {
            if (os === 'Windows') {
                code.textContent = code.textContent.replace('&&', '&');
            }
        }
    });
});

// Easter egg: Konami code
let konamiCode = [];
const konamiSequence = [38, 38, 40, 40, 37, 39, 37, 39, 66, 65]; // ↑↑↓↓←→←→BA

document.addEventListener('keydown', function(e) {
    konamiCode.push(e.keyCode);
    
    if (konamiCode.length > konamiSequence.length) {
        konamiCode.shift();
    }
    
    if (konamiCode.length === konamiSequence.length) {
        if (konamiCode.every((code, index) => code === konamiSequence[index])) {
            showEasterEgg();
        }
    }
});

function showEasterEgg() {
    const easterEgg = document.createElement('div');
    easterEgg.innerHTML = `
        <div style="text-align: center;">
            <h2>🎉 ¡Konami Code activado!</h2>
            <p>🔐 Modo Desarrollador Desbloqueado</p>
            <p style="font-family: monospace; color: #6ab04c;">
                System.out.println("¡Hola desde el código secreto!");
            </p>
        </div>
    `;
    easterEgg.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 40px;
        border-radius: 15px;
        box-shadow: 0 15px 35px rgba(0,0,0,0.3);
        z-index: 1001;
        max-width: 400px;
        text-align: center;
    `;
    
    const overlay = document.createElement('div');
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.7);
        z-index: 1000;
    `;
    
    document.body.appendChild(overlay);
    document.body.appendChild(easterEgg);
    
    // Cerrar al hacer click
    overlay.addEventListener('click', function() {
        document.body.removeChild(overlay);
        document.body.removeChild(easterEgg);
        konamiCode = [];
    });
    
    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        if (document.body.contains(overlay)) {
            document.body.removeChild(overlay);
            document.body.removeChild(easterEgg);
            konamiCode = [];
        }
    }, 5000);
}
